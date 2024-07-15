package com.hhplus.hhplusconcert.domain.concert.service.dto;

import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import com.hhplus.hhplusconcert.domain.payment.enums.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ReservationPaymentInfo(Long paymentId, PaymentStatus status,
                                     BigDecimal paymentPrice) {
    public static ReservationPaymentInfo of(Payment payment) {
        return ReservationPaymentInfo.builder()
                .paymentId(payment.getPaymentId())
                .paymentPrice(payment.getPaymentPrice())
                .status(payment.getStatus())
                .build();
    }
}
