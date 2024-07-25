package com.hhplus.hhplusconcert.domain.payment;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository {
    // Payment 관련
    Payment createPayment(Payment payment);

    Optional<Payment> findByReservationId(Long reservationId);

    void deleteAll();

    Optional<Payment> getPayment(Long reservationId);

    List<Payment> getPayments();


    Optional<Payment> savePayment(Payment payment);
}
