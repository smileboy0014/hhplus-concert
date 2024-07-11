package com.hhplus.hhplusconcert.infrastructure.payment;

import com.hhplus.hhplusconcert.domain.common.exception.CustomNotFoundException;
import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import com.hhplus.hhplusconcert.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.PAYMENT_IS_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;


    @Override
    public Payment createPayment(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Payment findByReservationId(Long reservationId) {
        return paymentJpaRepository.findByReservation_reservationId(reservationId)
                .orElseThrow(() -> new CustomNotFoundException(PAYMENT_IS_NOT_FOUND,
                        "해당 예약에 대한 결제 내역을 조회할 수 없습니다. [reservationId: %d]".formatted(reservationId)));
    }

    @Override
    public void deleteAll() {
        paymentJpaRepository.deleteAllInBatch();
    }

}
