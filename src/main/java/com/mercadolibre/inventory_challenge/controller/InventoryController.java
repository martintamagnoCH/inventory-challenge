package com.mercadolibre.inventory_challenge.controller;

import com.mercadolibre.inventory_challenge.Service.InventoryService;
import com.mercadolibre.inventory_challenge.dto.StockMovementRequest;
import com.mercadolibre.inventory_challenge.dto.StockUpdateRequest;
import com.mercadolibre.inventory_challenge.model.Inventory;
import com.mercadolibre.inventory_challenge.model.StockMovement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public List<Inventory> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    @GetMapping("/{sku}")
    public List<Inventory> getInventoryBySku(@PathVariable String sku) {
        return inventoryService.getInventoryBySku(sku);
    }

    @PostMapping("/update")
    public ResponseEntity<Inventory> updateStock(@RequestBody @Valid StockUpdateRequest request) {
        logger.info("Received stock update request: {}", request);
        Inventory updated = inventoryService.updateStock(request.getSku(), request.getStoreId(), request.getNewStock());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/movement")
    public ResponseEntity<StockMovement> registerMovement(@RequestBody @Valid StockMovementRequest request) {
        logger.info("Received stock movement request: {}", request);
        StockMovement movement = inventoryService.registerMovement(request.getSku(), request.getStoreId(), request.getType(), request.getQuantity());
        return ResponseEntity.ok(movement);
    }
}