package com.hhplus.hhplusconcert.infrastructure.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByConcertReservation_reservationId(Long reservationId);


    @Query("SELECT p FROM PaymentEntity p " +
            "JOIN FETCH p.concertReservation r ")
    List<PaymentEntity> findPayments();
}
