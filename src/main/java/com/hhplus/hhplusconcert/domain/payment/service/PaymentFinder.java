package com.hhplus.hhplusconcert.domain.payment.service;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import com.hhplus.hhplusconcert.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.PAYMENT_IS_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class PaymentFinder {
    private final PaymentRepository paymentRepository;

    public Payment findPaymentByReservationId(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new CustomException(PAYMENT_IS_NOT_FOUND,
                        "해당 예약에 대한 결제 내역을 조회할 수 없습니다. [reservationId: %d]".formatted(reservationId)));

    }
}
