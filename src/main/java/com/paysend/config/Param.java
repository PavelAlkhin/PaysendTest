package com.paysend.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payments")
@AllArgsConstructor
@Getter
public class Param {
    private final String paymentUrl;
    private final String token;
}
