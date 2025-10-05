package com.mercadolibre.inventory_challenge.controller;

import com.mercadolibre.inventory_challenge.Service.InventoryService;
import com.mercadolibre.inventory_challenge.dto.StockMovementRequest;
import com.mercadolibre.inventory_challenge.dto.StockUpdateRequest;
import com.mercadolibre.inventory_challenge.model.Inventory;
import com.mercadolibre.inventory_challenge.model.StockMovement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

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
        Inventory updated = inventoryService.updateStock(request.getSku(), request.getStoreId(), request.getNewStock());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/movement")
    public ResponseEntity<StockMovement> registerMovement(@RequestBody @Valid StockMovementRequest request) {
        StockMovement movement = inventoryService.registerMovement(request.getSku(), request.getStoreId(), request.getType(), request.getQuantity());
        return ResponseEntity.ok(movement);
    }
}