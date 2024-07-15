package com.hhplus.hhplusconcert.domain.payment.service.dto;

import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import com.hhplus.hhplusconcert.domain.payment.enums.PaymentStatus;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentInfo(Long paymentId, PaymentStatus status, BigDecimal paymentPrice,
                          BigDecimal balance) {

    public static PaymentInfo of(Payment payment, User user) {

        return PaymentInfo
                .builder()
                .paymentId(payment.getPaymentId())
                .status(payment.getStatus())
                .paymentPrice(payment.getPaymentPrice())
                .balance(user.getBalance())
                .build();
    }

}
