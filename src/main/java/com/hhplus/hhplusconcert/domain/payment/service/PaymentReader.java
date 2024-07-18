package com.hhplus.hhplusconcert.domain.payment.service;

import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import com.hhplus.hhplusconcert.domain.payment.service.dto.PaymentInfo;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentReader {

    public PaymentInfo readPayment(Payment payment, User user) {
        return PaymentInfo.of(payment, user);
    }
}
