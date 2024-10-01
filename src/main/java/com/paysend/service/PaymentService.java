package com.paysend.service;

import com.paysend.dto.PayResponseDto;
import com.paysend.dto.RequestAmountDto;

public interface PaymentService {
    PayResponseDto sendGet(RequestAmountDto dto);
}
