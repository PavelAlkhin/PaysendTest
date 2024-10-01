package com.paysend.dto;

public record PayRequestDto(
        PaymentType paymentType,
        Double amount,
        String currency,
        Customer customer) {
    public record Customer(String referenceId) {
    }
}
