package com.mercadolibre.inventory_challenge.Service;

import com.mercadolibre.inventory_challenge.exception.InventoryException;
import com.mercadolibre.inventory_challenge.model.Inventory;
import com.mercadolibre.inventory_challenge.model.StockMovement;
import com.mercadolibre.inventory_challenge.repository.InventoryRepository;
import com.mercadolibre.inventory_challenge.repository.StockMovementRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import java.time.LocalDateTime;

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public List<Inventory> getInventoryBySku(String sku) {
        return inventoryRepository.findBySku(sku);
    }

    @Transactional
    public Inventory updateStock(String sku, String storeId, Integer newStock) {
        Inventory inventory = inventoryRepository.findBySkuAndStoreId(sku, storeId)
                .orElseThrow(() -> new InventoryException("Inventory not found for SKU: " + sku + " and Store ID: " + storeId));
        inventory.setQuantity(newStock);
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public StockMovement registerMovement(String sku, String storeId, String type, Integer quantity) {
        // Buscar el inventario
        Inventory inventory = inventoryRepository.findAll().stream()
                .filter(inv -> inv.getSku().equals(sku) && inv.getStoreId().equals(storeId))
                .findFirst()
                .orElse(Inventory.builder().sku(sku).storeId(storeId).quantity(0).build());

        if ("sale".equalsIgnoreCase(type)) {
            if (inventory.getQuantity() < quantity) throw new IllegalArgumentException("No hay suficiente stock");
            inventory.setQuantity(inventory.getQuantity() - quantity);
        } else if ("restock".equalsIgnoreCase(type)) {
            inventory.setQuantity(inventory.getQuantity() + quantity);
        } else {
            throw new IllegalArgumentException("Tipo de movimiento no soportado");
        }

        inventoryRepository.save(inventory);

        StockMovement movement = StockMovement.builder()
                .sku(sku)
                .storeId(storeId)
                .type(type)
                .quantity(quantity)
                .timestamp(LocalDateTime.now())
                .build();

        return stockMovementRepository.save(movement);
    }
}