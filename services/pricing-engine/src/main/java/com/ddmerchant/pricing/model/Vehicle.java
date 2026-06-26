package com.ddmerchant.pricing.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long modelId;
    private Long shopId;
    private String externalVehicleId;
    private String status;
    private Double currentPrice;
    private Instant createdAt;

    // getters/setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getModelId() { return modelId; }
    public void setModelId(Long modelId) { this.modelId = modelId; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public String getExternalVehicleId() { return externalVehicleId; }
    public void setExternalVehicleId(String externalVehicleId) { this.externalVehicleId = externalVehicleId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(Double currentPrice) { this.currentPrice = currentPrice; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
