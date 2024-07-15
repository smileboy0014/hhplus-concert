package com.hhplus.hhplusconcert.domain.payment.repository;

import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository {
    // Payment 관련
    Payment createPayment(Payment payment);

    Payment findByReservationId(Long reservationId);

    void deleteAll();

}
