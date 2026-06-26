package com.ddmerchant.pricing.client;

import org.springframework.stereotype.Component;

@Component
public class MockDidiClient implements DidiClient {
    @Override
    public boolean updatePrice(String externalVehicleId, double newPrice) {
        System.out.println("[MockDidiClient] updatePrice vehicle=" + externalVehicleId + " price=" + newPrice);
        return true;
    }
}
