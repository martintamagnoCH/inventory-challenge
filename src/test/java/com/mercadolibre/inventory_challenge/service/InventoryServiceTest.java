package com.mercadolibre.inventory_challenge.service;

import com.mercadolibre.inventory_challenge.Service.InventoryService;
import com.mercadolibre.inventory_challenge.exception.InventoryException;
import com.mercadolibre.inventory_challenge.model.Inventory;
import com.mercadolibre.inventory_challenge.model.StockMovement;
import com.mercadolibre.inventory_challenge.repository.InventoryRepository;
import com.mercadolibre.inventory_challenge.repository.StockMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllInventory() {
        Inventory inv = Inventory.builder().sku("SKU1").storeId("Tienda1").quantity(10).build();
        when(inventoryRepository.findAll()).thenReturn(List.of(inv));

        List<Inventory> result = inventoryService.getAllInventory();

        assertEquals(1, result.size());
        assertEquals("SKU1", result.get(0).getSku());
        assertEquals("Tienda1", result.get(0).getStoreId());
        assertEquals(10, result.get(0).getQuantity());
    }

    @Test
    void testGetInventoryBySku() {
        Inventory inv = Inventory.builder().sku("SKU2").storeId("Tienda2").quantity(Integer.valueOf(20)).build();
        when(inventoryRepository.findBySku("SKU2")).thenReturn(List.of(inv));

        List<Inventory> result = inventoryService.getInventoryBySku("SKU2");

        assertEquals(1, result.size());
        assertEquals("SKU2", result.get(0).getSku());
        assertEquals("Tienda2", result.get(0).getStoreId());
        assertEquals(20, result.get(0).getQuantity());
    }

    @Test
    void testUpdateStock_success() {
        Inventory inv = Inventory.builder().sku("SKU1").storeId("Tienda1").quantity(Integer.valueOf(5)).build();
        when(inventoryRepository.findBySkuAndStoreId("SKU1", "Tienda1")).thenReturn(Optional.of(inv));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));

        Inventory result = inventoryService.updateStock("SKU1", "Tienda1", Integer.valueOf(12));

        assertEquals("SKU1", result.getSku());
        assertEquals("Tienda1", result.getStoreId());
        assertEquals(12, result.getQuantity());
        verify(inventoryRepository).save(inv);
    }

    @Test
    void testUpdateStock_inventoryNotFound() {
        when(inventoryRepository.findBySkuAndStoreId(anyString(), anyString())).thenReturn(Optional.empty());

        InventoryException ex = assertThrows(InventoryException.class, () ->
                inventoryService.updateStock("SKU_X", "Tienda_X", Integer.valueOf(13)));
        assertTrue(ex.getMessage().contains("Inventory not found"));
    }

    @Test
    void testRegisterMovement_sale_success() {
        Inventory inv = Inventory.builder().sku("SKU1").storeId("Tienda1").quantity(8).build();
        when(inventoryRepository.findAll()).thenReturn(List.of(inv));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));
        StockMovement expectedMovement = StockMovement.builder()
                .sku("SKU1").storeId("Tienda1").type("sale").quantity(3)
                .timestamp(LocalDateTime.now()).build();
        when(stockMovementRepository.save(any(StockMovement.class))).thenReturn(expectedMovement);

        StockMovement result = inventoryService.registerMovement("SKU1", "Tienda1", "sale", 3);

        assertEquals("SKU1", result.getSku());
        assertEquals("sale", result.getType());
        assertEquals(3, result.getQuantity());
        verify(inventoryRepository).save(inv);
        verify(stockMovementRepository).save(any(StockMovement.class));
        assertEquals(5, inv.getQuantity());
    }

    @Test
    void testRegisterMovement_restock_success() {
        Inventory inv = Inventory.builder().sku("SKU2").storeId("Tienda2").quantity(2).build();
        when(inventoryRepository.findAll()).thenReturn(List.of(inv));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));
        StockMovement expectedMovement = StockMovement.builder()
                .sku("SKU2").storeId("Tienda2").type("restock").quantity(7)
                .timestamp(LocalDateTime.now()).build();
        when(stockMovementRepository.save(any(StockMovement.class))).thenReturn(expectedMovement);

        StockMovement result = inventoryService.registerMovement("SKU2", "Tienda2", "restock", 7);

        assertEquals("restock", result.getType());
        assertEquals(9, inv.getQuantity());
    }

    @Test
    void testRegisterMovement_sale_insufficientStock() {
        Inventory inv = Inventory.builder().sku("SKU1").storeId("Tienda1").quantity(1).build();
        when(inventoryRepository.findAll()).thenReturn(List.of(inv));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                inventoryService.registerMovement("SKU1", "Tienda1", "sale", 10));
        assertEquals("No hay suficiente stock", ex.getMessage());
    }

    @Test
    void testRegisterMovement_unsupportedType() {
        Inventory inv = Inventory.builder().sku("SKU3").storeId("Tienda3").quantity(5).build();
        when(inventoryRepository.findAll()).thenReturn(List.of(inv));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                inventoryService.registerMovement("SKU3", "Tienda3", "unknown", 1));
        assertEquals("Tipo de movimiento no soportado", ex.getMessage());
    }

    @Test
    void testRegisterMovement_newInventoryIfNotExists() {
        when(inventoryRepository.findAll()).thenReturn(List.of());
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));
        when(stockMovementRepository.save(any(StockMovement.class))).thenAnswer(i -> i.getArgument(0));

        StockMovement result = inventoryService.registerMovement("SKU_NEW", "Store_NEW", "restock", 100);

        assertEquals("SKU_NEW", result.getSku());
        assertEquals("Store_NEW", result.getStoreId());
        assertEquals("restock", result.getType());
    }
}