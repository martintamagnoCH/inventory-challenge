package com.mercadolibre.inventory_challenge.repository;

import com.mercadolibre.inventory_challenge.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findBySku(String sku);
    List<Inventory> findByStoreId(String storeId);
}