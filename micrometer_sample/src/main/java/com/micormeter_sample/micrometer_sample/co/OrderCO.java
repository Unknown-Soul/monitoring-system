package com.micormeter_sample.micrometer_sample.co;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCO {

    @NotEmpty(message = "Order must have at least one product")
    @Valid
    private List<OrderItemData> items;

    @NotNull(message = "customer name cannot be null")
    private String customerName;

    @DecimalMax(value = "100.0", message = "Discount cannot exceed 100%")
    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    private BigDecimal discount;

    @Data
    public static class OrderItemData {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;
    }
}
