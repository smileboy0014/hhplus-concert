package com.hhplus.hhplusconcert.infrastructure.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByConcertReservation_reservationId(Long reservationId);

}
