package com.ddmerchant.pricing.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "price_history")
public class PriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long vehicleId;
    private Long modelId;
    private Double oldPrice;
    private Double newPrice;
    private String reason;
    private String triggerSource;
    private String operator;
    private Instant createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    public Long getModelId() { return modelId; }
    public void setModelId(Long modelId) { this.modelId = modelId; }
    public Double getOldPrice() { return oldPrice; }
    public void setOldPrice(Double oldPrice) { this.oldPrice = oldPrice; }
    public Double getNewPrice() { return newPrice; }
    public void setNewPrice(Double newPrice) { this.newPrice = newPrice; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getTriggerSource() { return triggerSource; }
    public void setTriggerSource(String triggerSource) { this.triggerSource = triggerSource; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
