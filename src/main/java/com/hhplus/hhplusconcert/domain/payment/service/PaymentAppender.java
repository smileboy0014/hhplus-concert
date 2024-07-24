package com.hhplus.hhplusconcert.domain.payment.service;

import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import com.hhplus.hhplusconcert.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentAppender {

    private final PaymentRepository paymentRepository;

    public Payment appendPayment(Payment payment) {
        return paymentRepository.createPayment(payment);
    }

    public void deleteAll() {
        paymentRepository.deleteAll();
    }
}
