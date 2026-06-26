package com.ddmerchant.pricing.repository;

import com.ddmerchant.pricing.model.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {
    List<PricingRule> findByScopeAndTargetIdAndActive(String scope, Long targetId, Boolean active);
    List<PricingRule> findByScopeAndActive(String scope, Boolean active);
}
