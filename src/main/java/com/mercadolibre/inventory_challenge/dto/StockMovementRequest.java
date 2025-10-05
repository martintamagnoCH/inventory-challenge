package com.mercadolibre.inventory_challenge.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockMovementRequest {
    @NotNull(message = "SKU cannot be null")
    private String sku;
    @NotNull(message = "Store ID cannot be null")
    private String storeId;
    @NotNull(message = "Type cannot be null")
    private String type; // "sale" o "restock"
    @NotNull(message = "Quantity cannot be null")
    private Integer quantity;
}