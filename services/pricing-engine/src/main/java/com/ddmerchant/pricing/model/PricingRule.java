package com.ddmerchant.pricing.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pricing_rules")
public class PricingRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String scope;
    private Long targetId;
    private String type;

    @Column(columnDefinition = "jsonb")
    private String params; // store JSON as string

    private Boolean active;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getParams() { return params; }
    public void setParams(String params) { this.params = params; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
