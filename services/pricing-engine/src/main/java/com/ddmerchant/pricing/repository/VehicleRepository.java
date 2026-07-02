package com.ddmerchant.pricing.repository;

import com.ddmerchant.pricing.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByShopIdAndModelId(Long shopId, Long modelId);
    List<Vehicle> findByModelId(Long modelId);
}
