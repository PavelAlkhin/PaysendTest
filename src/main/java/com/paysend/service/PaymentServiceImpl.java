package com.paysend.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paysend.config.Param;
import com.paysend.dto.PayRequestDto;
import com.paysend.dto.PayResponseDto;
import com.paysend.dto.RequestAmountDto;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final Param param;
    private final RestTemplate restTemplate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public PayResponseDto sendGet(RequestAmountDto request) {

        PayRequestDto dto = new PayRequestDto(
                request.paymentType(),
                request.amount(),
                request.currency(),
                new PayRequestDto.Customer(request.customer())
        );

        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(param.getToken());

        HttpEntity<PayRequestDto> payRequestDtoHttpEntity = new HttpEntity<>(dto, headers);

        try {
            ResponseEntity<String> exchange = restTemplate.exchange(URI.create(param.getPaymentUrl()), HttpMethod.POST, payRequestDtoHttpEntity, String.class);
            if (!exchange.getStatusCode().is2xxSuccessful()) {
                logger.error(String.valueOf(exchange.getBody()));
                throw new RuntimeException(exchange.getBody());
            }
            return om.readValue(exchange.getBody(), PayResponseDto.class);
        } catch (Exception e) {
            logger.error("Payment error", e);
            return new PayResponseDto(null, 0, null, e.getMessage());
        }

    }
}
