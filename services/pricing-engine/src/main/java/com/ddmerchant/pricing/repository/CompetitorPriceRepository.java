package com.ddmerchant.pricing.repository;

import com.ddmerchant.pricing.model.CompetitorPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface CompetitorPriceRepository extends JpaRepository<CompetitorPrice, Long> {
    List<CompetitorPrice> findByModelIdAndShopIdAndCollectedAtAfter(Long modelId, Long shopId, Instant after);
    List<CompetitorPrice> findByModelIdAndCollectedAtAfter(Long modelId, Instant after);
}
