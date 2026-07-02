package com.ddmerchant.pricing.service;

import com.ddmerchant.pricing.client.DidiClient;
import com.ddmerchant.pricing.model.CompetitorPrice;
import com.ddmerchant.pricing.model.PriceHistory;
import com.ddmerchant.pricing.model.Vehicle;
import com.ddmerchant.pricing.repository.CompetitorPriceRepository;
import com.ddmerchant.pricing.repository.PriceHistoryRepository;
import com.ddmerchant.pricing.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PricingEngineServiceTest {

    private VehicleRepository vehicleRepository;
    private CompetitorPriceRepository competitorPriceRepository;
    private PriceHistoryRepository priceHistoryRepository;
    private DidiClient didiClient;
    private CompetitorAggregatorService aggregatorService;
    private PricingEngineService pricingEngineService;

    @BeforeEach
    public void setup() {
        vehicleRepository = mock(VehicleRepository.class);
        competitorPriceRepository = mock(CompetitorPriceRepository.class);
        priceHistoryRepository = mock(PriceHistoryRepository.class);
        didiClient = mock(DidiClient.class);

        aggregatorService = new CompetitorAggregatorService(competitorPriceRepository);
        pricingEngineService = new PricingEngineService(vehicleRepository, aggregatorService, didiClient, priceHistoryRepository, mock(com.ddmerchant.pricing.repository.PricingRuleRepository.class));
    }

    @Test
    public void testApplyPricing_basicFlow() {
        Vehicle v = new Vehicle();
        v.setId(1L);
        v.setModelId(10L);
        v.setShopId(100L);
        v.setExternalVehicleId("veh-1");
        v.setCurrentPrice(100.0);

        when(vehicleRepository.findByShopIdAndModelId(100L, 10L)).thenReturn(List.of(v));
        when(competitorPriceRepository.findByModelIdAndShopIdAndCollectedAtAfter(eq(10L), eq(100L), any())).thenReturn(List.of(
                createCompetitor(10L,100L,120.0),
                createCompetitor(10L,100L,110.0),
                createCompetitor(10L,100L,90.0),
                createCompetitor(10L,100L,95.0),
                createCompetitor(10L,100L,92.0)
        ));
        when(priceHistoryRepository.findTopByVehicleIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.empty());
        when(didiClient.updatePrice("veh-1", 85.0)).thenReturn(true);

        pricingEngineService.recalculate(100L,10L);

        ArgumentCaptor<Vehicle> vc = ArgumentCaptor.forClass(Vehicle.class);
        verify(vehicleRepository, times(1)).save(vc.capture());
        Vehicle saved = vc.getValue();
        assertEquals(85.0, saved.getCurrentPrice());
    }

    private CompetitorPrice createCompetitor(Long modelId, Long shopId, double price) {
        CompetitorPrice cp = new CompetitorPrice();
        cp.setModelId(modelId);
        cp.setShopId(shopId);
        cp.setPrice(price);
        cp.setCollectedAt(Instant.now());
        return cp;
    }
}
