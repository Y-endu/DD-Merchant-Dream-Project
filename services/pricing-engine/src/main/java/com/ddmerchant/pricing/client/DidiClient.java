package com.ddmerchant.pricing.client;

public interface DidiClient {
    /**
     * Update price of a vehicle in Didi platform.
     * Implementations must be idempotent and handle rate limits.
     */
    boolean updatePrice(String externalVehicleId, double newPrice);
}
