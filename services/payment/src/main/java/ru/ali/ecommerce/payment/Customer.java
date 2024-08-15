package ru.ali.ecommerce.payment;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public record Customer(
        String id,
        @NotNull(message = "Firstname required")
        String firstname,
        @NotNull(message = "Lastname required")
        String lastname,
        @NotNull(message = "Email required")
        @Email(message = "The customer email isn't correctly formatted")
        String email
) {
}
