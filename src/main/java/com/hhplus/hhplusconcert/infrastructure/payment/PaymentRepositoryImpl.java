package com.hhplus.hhplusconcert.infrastructure.payment;

import com.hhplus.hhplusconcert.domain.payment.Payment;
import com.hhplus.hhplusconcert.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment createPayment(Payment payment) {
        return null;
    }

    @Override
    public Optional<Payment> findByReservationId(Long reservationId) {
        return Optional.empty();
    }

    @Override
    public void deleteAll() {
        paymentJpaRepository.deleteAllInBatch();
    }

    @Override
    public Optional<Payment> getPayment(Long reservationId) {
        Optional<PaymentEntity> paymentEntity = paymentJpaRepository.findByConcertReservation_reservationId(reservationId);
        if (paymentEntity.isPresent()) {
            return paymentEntity.map(PaymentEntity::toDomain);
        }
        return Optional.empty();
    }

    @Override
    public List<Payment> getPayments() {
        return paymentJpaRepository.findPayments()
                .stream()
                .map(PaymentEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Payment> savePayment(Payment payment) {
        PaymentEntity paymentEntity = paymentJpaRepository.save(PaymentEntity.toEntity(payment));

        return Optional.of(paymentEntity.toDomain());
    }

}
