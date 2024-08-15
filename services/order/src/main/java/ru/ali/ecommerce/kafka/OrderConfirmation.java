package ru.ali.ecommerce.kafka;

import ru.ali.ecommerce.customer.CustomerResponse;
import ru.ali.ecommerce.order.PaymentMethod;
import ru.ali.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
