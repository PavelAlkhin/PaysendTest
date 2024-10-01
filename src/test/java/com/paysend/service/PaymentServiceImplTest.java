package com.paysend.service;

import com.paysend.config.Param;
import com.paysend.dto.PayResponseDto;
import com.paysend.dto.PaymentType;
import com.paysend.dto.RequestAmountDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class PaymentServiceImplTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private PaymentServiceImpl paymentService;

    @Autowired
    Param param;

    ResponseEntity<String> responseEntityPositive = ResponseEntity.ok("{\n" +
            "  \"timestamp\": \"2024-10-01T05:30:41.300+00:00\",\n" +
            "  \"status\": 200,\n" +
            "  \"result\": {\n" +
            "    \"id\": \"1835df11e5dd44268f26a075bc7c10d0\",\n" +
            "    \"referenceId\": \"payment_id=123;custom_ref=456\",\n" +
            "    \"paymentType\": \"DEPOSIT\",\n" +
            "    \"state\": \"CHECKOUT\",\n" +
            "    \"amount\": 11.12,\n" +
            "    \"currency\": \"EUR\",\n" +
            "    \"redirectUrl\": \"https://engine-sandbox.pay.tech/m/payment/1835df11e5dd44268f26a075bc7c10d0\",\n" +
            "    \"customer\": {\n" +
            "      \"referenceId\": \"customer_123\"\n" +
            "    }\n" +
            "  }\n" +
            "}");

    ResponseEntity<String> responseEntityNegative = new ResponseEntity<String>("{\n" +
            "  \"timestamp\": \"2024-10-01T06:26:33.514+00:00\",\n" +
            "  \"status\": 400,\n" +
            "  \"error\": \"Bad Request\",\n" +
            "  \"message\": \"Validation failed for object='paymentRequest'. Error count: 1\",\n" +
            "  \"errors\": [\n" +
            "    {\n" +
            "      \"codes\": [\n" +
            "        \"NotNull.paymentRequest.currency\",\n" +
            "        \"NotNull.currency\",\n" +
            "        \"NotNull.com.neopay.model.Currency\",\n" +
            "        \"NotNull\"\n" +
            "      ],\n" +
            "      \"arguments\": [\n" +
            "        {\n" +
            "          \"codes\": [\n" +
            "            \"paymentRequest.currency\",\n" +
            "            \"currency\"\n" +
            "          ],\n" +
            "          \"defaultMessage\": \"currency\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"defaultMessage\": \"не должно равняться null\",\n" +
            "      \"objectName\": \"paymentRequest\",\n" +
            "      \"field\": \"currency\",\n" +
            "      \"bindingFailure\": false\n" +
            "    }\n" +
            "  ],\n" +
            "  \"path\": \"/api/v1/payments\"\n" +
            "}", HttpStatusCode.valueOf(400));

    @Test
    void sendGetPositive() {

        when(restTemplate.exchange(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<String>>any()))
                .thenReturn(responseEntityPositive);

        PayResponseDto payResponseDto = paymentService.sendGet(new RequestAmountDto(PaymentType.DEPOSIT, 12.0, "EUR", "Customer"));

        Assertions.assertEquals("https://engine-sandbox.pay.tech/m/payment/1835df11e5dd44268f26a075bc7c10d0", payResponseDto.result().redirectUrl());
        Assertions.assertNull(payResponseDto.errorText());
    }

    @Test
    void getNegative() {

        when(restTemplate.exchange(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<String>>any()))
                .thenReturn(responseEntityNegative);

        PayResponseDto payResponseDto = paymentService.sendGet(new RequestAmountDto(PaymentType.DEPOSIT, 12.0, "EUR", "Customer"));

        Assertions.assertNotNull(payResponseDto.errorText());
        Assertions.assertNull(payResponseDto.result());
    }
}