package com.ddmerchant.pricing.service;

import com.ddmerchant.pricing.repository.CompetitorPriceRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompetitorAggregatorService {

    private final CompetitorPriceRepository competitorPriceRepository;

    public CompetitorAggregatorService(CompetitorPriceRepository competitorPriceRepository) {
        this.competitorPriceRepository = competitorPriceRepository;
    }

    /**
     * Compute a competitive price for a given model/shop using recent competitor prices.
     * Strategy: use 10th percentile if enough data, otherwise use min.
     */
    public Double computeCompetitivePrice(Long modelId, Long shopId, int lastMinutes) {
        Instant after = Instant.now().minusSeconds(lastMinutes * 60L);
        List<Double> prices;
        if (shopId != null) {
            prices = competitorPriceRepository.findByModelIdAndShopIdAndCollectedAtAfter(modelId, shopId, after)
                    .stream().map(p -> p.getPrice()).collect(Collectors.toList());
        } else {
            prices = competitorPriceRepository.findByModelIdAndCollectedAtAfter(modelId, after)
                    .stream().map(p -> p.getPrice()).collect(Collectors.toList());
        }
        if (prices.isEmpty()) return null;
        Collections.sort(prices);
        if (prices.size() < 5) {
            return prices.get(0); // small sample: return min
        }
        // 10th percentile
        int idx = (int) Math.ceil(0.1 * prices.size()) - 1;
        idx = Math.max(0, idx);
        return prices.get(idx);
    }
}
