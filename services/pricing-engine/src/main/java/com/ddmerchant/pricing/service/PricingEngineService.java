package com.ddmerchant.pricing.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PricingEngineService {

    // Placeholder: later inject repositories, clients, aggregators

    public void recalculate(Long shopId, Long modelId) {
        // Start background recalculation (sync/blocking for now)
        System.out.println("Recalculating prices for shop=" + shopId + " model=" + modelId);
        // TODO: load vehicles, fetch competitor prices, compute target price, persist history, call Didi client
    }

    public void overridePrice(Map<String,Object> body) {
        System.out.println("Override request: " + body);
        // TODO: validate, persist price_history, call didi client
    }
}
