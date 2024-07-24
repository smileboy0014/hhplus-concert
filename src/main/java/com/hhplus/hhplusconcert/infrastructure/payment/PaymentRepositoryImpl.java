package com.hhplus.hhplusconcert.infrastructure.payment;

import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import com.hhplus.hhplusconcert.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;


    @Override
    public Payment createPayment(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findByReservationId(Long reservationId) {
        return paymentJpaRepository.findByReservation_reservationId(reservationId);
    }

    @Override
    public void deleteAll() {
        paymentJpaRepository.deleteAllInBatch();
    }

}
