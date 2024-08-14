package ru.ali.ecommerce.product;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductPurchaseResponse(

        Integer productId,
        String name,
        String description,
        BigDecimal price,
        double quantity
) {
}
