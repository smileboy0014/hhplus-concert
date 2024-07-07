package com.hhplus.hhplusconcert.domain.payment.service.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentResponse(Long paymentId, String status, BigDecimal paymentPrice,
                              BigDecimal balance) {
}
