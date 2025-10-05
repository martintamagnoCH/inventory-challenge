package com.mercadolibre.inventory_challenge.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class StockUpdateRequest {
    @NotNull(message = "SKU cannot be null")
    private String sku;
    @NotNull(message = "Store ID cannot be null")
    private String storeId;
    @NotNull(message = "New stock cannot be null")
    private Integer newStock;
}