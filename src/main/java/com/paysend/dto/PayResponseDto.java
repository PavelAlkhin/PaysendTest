package com.paysend.dto;

import java.util.Date;


public record PayResponseDto(
        Date timestamp,
        int status,
        ResponseResultDto result,
        String errorText) {

    public record ResponseResultDto(
            String id,
            String paymentType,
            String state,
            double amount,
            String currency,
            String redirectUrl,
            PayResponseDto.Customer customer) {
    }

    public record Customer(String referenceId) {
    }
}
