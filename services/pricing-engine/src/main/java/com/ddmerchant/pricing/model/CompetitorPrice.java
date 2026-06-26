package com.ddmerchant.pricing.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "competitor_prices")
public class CompetitorPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long modelId;
    private Long shopId;
    private String source;
    private Double price;
    private Instant collectedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getModelId() { return modelId; }
    public void setModelId(Long modelId) { this.modelId = modelId; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Instant getCollectedAt() { return collectedAt; }
    public void setCollectedAt(Instant collectedAt) { this.collectedAt = collectedAt; }
}
