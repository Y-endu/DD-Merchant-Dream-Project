package com.ddmerchant.pricing.service;

import com.ddmerchant.pricing.client.DidiClient;
import com.ddmerchant.pricing.model.PriceHistory;
import com.ddmerchant.pricing.model.PricingRule;
import com.ddmerchant.pricing.model.Vehicle;
import com.ddmerchant.pricing.repository.PriceHistoryRepository;
import com.ddmerchant.pricing.repository.PricingRuleRepository;
import com.ddmerchant.pricing.repository.VehicleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PricingEngineService {

    private final VehicleRepository vehicleRepository;
    private final CompetitorAggregatorService aggregatorService;
    private final DidiClient didiClient;
    private final PriceHistoryRepository priceHistoryRepository;
    private final PricingRuleRepository pricingRuleRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PricingEngineService(VehicleRepository vehicleRepository,
                                CompetitorAggregatorService aggregatorService,
                                DidiClient didiClient,
                                PriceHistoryRepository priceHistoryRepository,
                                PricingRuleRepository pricingRuleRepository) {
        this.vehicleRepository = vehicleRepository;
        this.aggregatorService = aggregatorService;
        this.didiClient = didiClient;
        this.priceHistoryRepository = priceHistoryRepository;
        this.pricingRuleRepository = pricingRuleRepository;
    }

    @Transactional
    public void recalculate(Long shopId, Long modelId) {
        System.out.println("Recalculating prices for shop=" + shopId + " model=" + modelId);
        List<Vehicle> vehicles;
        if (shopId != null && modelId != null) {
            vehicles = vehicleRepository.findByShopIdAndModelId(shopId, modelId);
        } else if (modelId != null) {
            vehicles = vehicleRepository.findByModelId(modelId);
        } else {
            // for demo: do nothing if no filter
            System.out.println("No model specified, skipping full scan in demo mode.");
            return;
        }

        for (Vehicle v : vehicles) {
            try {
                applyPricing(v);
            } catch (Exception ex) {
                System.err.println("Error applying pricing for vehicle " + v.getId() + ": " + ex.getMessage());
            }
        }
    }

    private void applyPricing(Vehicle v) throws Exception {
        Double current = v.getCurrentPrice();
        if (current == null) {
            System.out.println("Vehicle " + v.getId() + " has no current price, skipping.");
            return;
        }
        // fetch rule: model -> shop -> global
        PricingRule rule = fetchEffectiveRule(v.getModelId(), v.getShopId());
        double delta = 5.0; // default fixed delta
        double maxDropPct = 0.2; // default max drop 20%
        double floorPrice = 10.0; // default floor
        int competitorWindowMin = 15;
        double minChangeAmount = 0.5;
        if (rule != null && rule.getParams() != null) {
            JsonNode p = objectMapper.readTree(rule.getParams());
            if (p.has("delta")) delta = p.get("delta").asDouble(delta);
            if (p.has("maxDropPct")) maxDropPct = p.get("maxDropPct").asDouble(maxDropPct);
            if (p.has("floorPrice")) floorPrice = p.get("floorPrice").asDouble(floorPrice);
            if (p.has("competitorWindowMin")) competitorWindowMin = p.get("competitorWindowMin").asInt(competitorWindowMin);
            if (p.has("minChangeAmount")) minChangeAmount = p.get("minChangeAmount").asDouble(minChangeAmount);
        }

        Double pMin = aggregatorService.computeCompetitivePrice(v.getModelId(), v.getShopId(), competitorWindowMin);
        if (pMin == null) {
            System.out.println("No competitor price for model=" + v.getModelId() + ", skip pricing.");
            return;
        }
        double candidate = pMin - delta;
        double maxDropPrice = current * (1 - maxDropPct);
        double target = Math.max(floorPrice, Math.min(candidate, maxDropPrice));
        // enforce min change
        if (Math.abs(target - current) < minChangeAmount) {
            System.out.println("Change below min threshold for vehicle=" + v.getId() + ", skip. current=" + current + " target=" + target);
            return;
        }
        // check last change time (simple rate limit: not changed in last 30 minutes)
        Optional<PriceHistory> last = priceHistoryRepository.findTopByVehicleIdOrderByCreatedAtDesc(v.getId());
        if (last.isPresent()) {
            Instant lastTime = last.get().getCreatedAt();
            if (lastTime != null && lastTime.isAfter(Instant.now().minusSeconds(30 * 60L))) {
                System.out.println("Recently changed vehicle=" + v.getId() + ", skip to avoid frequent changes.");
                return;
            }
        }
        // call didi client to update
        boolean ok = didiClient.updatePrice(v.getExternalVehicleId(), roundToTwo(target));
        if (ok) {
            // persist history and update vehicle
            PriceHistory h = new PriceHistory();
            h.setVehicleId(v.getId());
            h.setModelId(v.getModelId());
            h.setOldPrice(current);
            h.setNewPrice(roundToTwo(target));
            h.setReason("auto_competitor_delta");
            h.setTriggerSource("pricing-engine");
            h.setOperator("system");
            h.setCreatedAt(Instant.now());
            priceHistoryRepository.save(h);

            v.setCurrentPrice(roundToTwo(target));
            vehicleRepository.save(v);
            System.out.println("Updated price for vehicle=" + v.getId() + " from " + current + " to " + target);
        } else {
            System.err.println("Failed to update price on Didi for vehicle=" + v.getId());
        }
    }

    private double roundToTwo(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private PricingRule fetchEffectiveRule(Long modelId, Long shopId) {
        // model
        List<PricingRule> modelRules = pricingRuleRepository.findByScopeAndTargetIdAndActive("model", modelId, true);
        if (!modelRules.isEmpty()) return modelRules.get(0);
        // shop
        List<PricingRule> shopRules = pricingRuleRepository.findByScopeAndTargetIdAndActive("shop", shopId, true);
        if (!shopRules.isEmpty()) return shopRules.get(0);
        // global
        List<PricingRule> globalRules = pricingRuleRepository.findByScopeAndActive("global", true);
        if (!globalRules.isEmpty()) return globalRules.get(0);
        return null;
    }

    public void overridePrice(Map<String,Object> body) {
        // simplistic override: expect vehicleId & newPrice & operator
        Long vehicleId = body.get("vehicleId") == null ? null : Long.valueOf(String.valueOf(body.get("vehicleId")));
        Double newPrice = body.get("newPrice") == null ? null : Double.valueOf(String.valueOf(body.get("newPrice")));
        String operator = body.get("operator") == null ? "operator" : String.valueOf(body.get("operator"));
        if (vehicleId == null || newPrice == null) return;
        Optional<Vehicle> ov = vehicleRepository.findById(vehicleId);
        if (ov.isEmpty()) return;
        Vehicle v = ov.get();
        Double old = v.getCurrentPrice();
        boolean ok = didiClient.updatePrice(v.getExternalVehicleId(), roundToTwo(newPrice));
        if (ok) {
            PriceHistory h = new PriceHistory();
            h.setVehicleId(v.getId());
            h.setModelId(v.getModelId());
            h.setOldPrice(old);
            h.setNewPrice(roundToTwo(newPrice));
            h.setReason("manual_override");
            h.setTriggerSource("api");
            h.setOperator(operator);
            h.setCreatedAt(Instant.now());
            priceHistoryRepository.save(h);
            v.setCurrentPrice(roundToTwo(newPrice));
            vehicleRepository.save(v);
        }
    }
}
