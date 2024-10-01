package com.paysend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record RequestAmountDto(
        PaymentType paymentType,
        @NotNull
        Double amount,
        @NotBlank
        String currency,
        @NotBlank
        String customer) implements Serializable {
}
