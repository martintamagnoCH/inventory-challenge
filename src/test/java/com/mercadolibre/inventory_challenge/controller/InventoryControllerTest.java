package com.mercadolibre.inventory_challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.inventory_challenge.Service.InventoryService;
import com.mercadolibre.inventory_challenge.dto.StockMovementRequest;
import com.mercadolibre.inventory_challenge.dto.StockUpdateRequest;
import com.mercadolibre.inventory_challenge.exception.InventoryException;
import com.mercadolibre.inventory_challenge.model.Inventory;
import com.mercadolibre.inventory_challenge.model.StockMovement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllInventory() throws Exception {
        Inventory inv = Inventory.builder().sku("SKU1").storeId("Tienda1").quantity(10).build();
        when(inventoryService.getAllInventory()).thenReturn(List.of(inv));

        mockMvc.perform(get("/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sku", is("SKU1")))
                .andExpect(jsonPath("$[0].storeId", is("Tienda1")))
                .andExpect(jsonPath("$[0].quantity", is(10)));
    }

    @Test
    void testGetInventoryBySku() throws Exception {
        Inventory inv = Inventory.builder().sku("SKU1").storeId("Tienda1").quantity(20).build();
        when(inventoryService.getInventoryBySku("SKU1")).thenReturn(List.of(inv));

        mockMvc.perform(get("/inventory/SKU1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sku", is("SKU1")))
                .andExpect(jsonPath("$[0].quantity", is(20)));
    }

    @Test
    void testUpdateStock_success() throws Exception {
        Inventory inv = Inventory.builder().sku("SKU1").storeId("Tienda1").quantity(15).build();
        when(inventoryService.updateStock(eq("SKU1"), eq("Tienda1"), eq(15))).thenReturn(inv);

        StockUpdateRequest req = new StockUpdateRequest();
        req.setSku("SKU1");
        req.setStoreId("Tienda1");
        req.setNewStock(15);

        mockMvc.perform(post("/inventory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku", is("SKU1")))
                .andExpect(jsonPath("$.quantity", is(15)));
    }

    @Test
    void testUpdateStock_validationError() throws Exception {
        StockUpdateRequest req = new StockUpdateRequest();
        req.setStoreId("Tienda1");
        req.setNewStock(15);

        mockMvc.perform(post("/inventory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterMovement_success() throws Exception {
        StockMovement movement = StockMovement.builder()
                .sku("SKU1").storeId("Tienda1").type("sale").quantity(2).build();

        when(inventoryService.registerMovement(eq("SKU1"), eq("Tienda1"), eq("sale"), eq(2)))
                .thenReturn(movement);

        StockMovementRequest req = StockMovementRequest.builder()
                .sku("SKU1")
                .storeId("Tienda1")
                .type("sale")
                .quantity(2)
                .build();

        mockMvc.perform(post("/inventory/movement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", is("sale")))
                .andExpect(jsonPath("$.quantity", is(2)));
    }

    @Test
    void testRegisterMovement_validationError() throws Exception {
        // Falta type
        StockMovementRequest req = StockMovementRequest.builder()
                .sku("SKU1")
                .storeId("Tienda1")
                .quantity(2)
                .build();

        mockMvc.perform(post("/inventory/movement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateStock_inventoryExceptionHandler() throws Exception {
        when(inventoryService.updateStock(anyString(), anyString(), anyInt()))
                .thenThrow(new InventoryException("Stock inválido"));

        String body = """
            {
                "sku": "SKU1",
                "storeId": "Tienda1",
                "newStock": 999
            }
            """;
        mockMvc.perform(post("/inventory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Stock inválido"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testUpdateStock_optimisticLockingHandler() throws Exception {
        when(inventoryService.updateStock(anyString(), anyString(), anyInt()))
                .thenThrow(new OptimisticLockingFailureException("Simulated conflict"));

        String body = """
            {
                "sku": "SKU1",
                "storeId": "Tienda1",
                "newStock": 123
            }
            """;
        mockMvc.perform(post("/inventory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Concurrency conflict: Resource was modified by another transaction"))
                .andExpect(jsonPath("$.status").value(409))
        ;
    }

    @Test
    void testUpdateStock_generalExceptionHandler() throws Exception {
        when(inventoryService.updateStock(anyString(), anyString(), anyInt()))
                .thenThrow(new RuntimeException("Error inesperado!"));

        String body = """
            {
                "sku": "SKU1",
                "storeId": "Tienda1",
                "newStock": 123
            }
            """;
        mockMvc.perform(post("/inventory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Unexpected error occurred"))
                .andExpect(jsonPath("$.details").value("Error inesperado!"))
                .andExpect(jsonPath("$.status").value(500));
    }
}