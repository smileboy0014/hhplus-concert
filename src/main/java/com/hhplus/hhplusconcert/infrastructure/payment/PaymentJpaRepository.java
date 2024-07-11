package com.hhplus.hhplusconcert.infrastructure.payment;

import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReservation_reservationId(Long reservationId);
}
