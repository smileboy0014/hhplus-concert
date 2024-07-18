package com.hhplus.hhplusconcert.domain.payment.repository;

import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository {
    // Payment 관련
    Payment createPayment(Payment payment);

    Optional<Payment> findByReservationId(Long reservationId);

    void deleteAll();

}
