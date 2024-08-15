package ru.ali.ecommerce.payment;

import ru.ali.ecommerce.customer.CustomerResponse;
import ru.ali.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(

        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer

) {
}
