package com.mercadolibre.inventory_challenge.repository;

import com.mercadolibre.inventory_challenge.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findBySku(String sku);
}