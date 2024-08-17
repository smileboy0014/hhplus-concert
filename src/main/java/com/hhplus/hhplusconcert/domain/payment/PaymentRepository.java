package com.hhplus.hhplusconcert.domain.payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    // Payment 관련
    Payment createPayment(Payment payment);

    Optional<Payment> findByReservationId(Long reservationId);

    void deleteAll();

    Optional<Payment> getPayment(Long reservationId);

    Optional<Payment> getCreatedPayment(Long paymentId);

    List<Payment> getPayments();


    Optional<Payment> savePayment(Payment payment);
}
