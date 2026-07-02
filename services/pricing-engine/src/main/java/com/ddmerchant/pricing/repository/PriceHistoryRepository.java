package com.ddmerchant.pricing.repository;

import com.ddmerchant.pricing.model.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    Optional<PriceHistory> findTopByVehicleIdOrderByCreatedAtDesc(Long vehicleId);
}
