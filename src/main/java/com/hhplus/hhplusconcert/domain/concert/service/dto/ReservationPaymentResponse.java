package com.hhplus.hhplusconcert.domain.concert.service.dto;

import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ReservationPaymentResponse(Long paymentId, String status,
                                         BigDecimal paymentPrice) {
    public static ReservationPaymentResponse of(Payment payment) {
        return ReservationPaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .paymentPrice(payment.getPaymentPrice())
                .status(payment.getStatus())
                .build();
    }
}
