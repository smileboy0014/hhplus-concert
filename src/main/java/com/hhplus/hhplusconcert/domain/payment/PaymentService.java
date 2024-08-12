package com.hhplus.hhplusconcert.domain.payment;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo;
import com.hhplus.hhplusconcert.domain.payment.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.PAYMENT_IS_FAILED;
import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher publisher;

    /**
     * 결제를 요청하면 결제 정보를 반환한다.
     *
     * @param reservationInfo 결제 요청 정보
     * @return PaymentResponse 결제 정보를 반환한다.
     */
    @Transactional
    public Payment pay(ConcertReservationInfo reservationInfo, String token) {

        Payment payment = Payment.builder()
                .concertReservationInfo(reservationInfo)
                .paymentPrice(reservationInfo.getSeatPrice())
                .paidAt(now())
                .status(Payment.PaymentStatus.COMPLETE).build();

        // 1. 결제 내역 생성
        Optional<Payment> completePayment = paymentRepository.savePayment(payment);

        if (completePayment.isEmpty()) {
            throw new CustomException(PAYMENT_IS_FAILED, "결제 완료 내역 생성에 실패하였습니다");
        }
        // 2. 결제 완료 이벤트 발행
        publisher.publishEvent(new PaymentEvent(this, reservationInfo, payment, token));

        return completePayment.get();
    }

    @Transactional
    public Payment cancelPayment(ConcertReservationInfo reservationCancelInfo) {
        // 1. 결제 내역 조회
        Optional<Payment> payment = paymentRepository.getPayment(reservationCancelInfo.getReservationId());

        // 1-1. 환불 처리
        if (payment.isPresent()) {
            payment.get().cancel();
            paymentRepository.savePayment(payment.get());

            return payment.get();
        }

        // 1-2. 결제 취소 내역 생성
        Optional<Payment> cancelPayment = paymentRepository.savePayment(Payment.builder()
                .concertReservationInfo(reservationCancelInfo)
                .status(Payment.PaymentStatus.CANCEL)
                .paymentPrice(BigDecimal.ZERO)
                .build());

        if (cancelPayment.isEmpty()) {
            throw new CustomException(PAYMENT_IS_FAILED, "결제 취소 내역 생성에 실패하였습니다");
        }

        return cancelPayment.get();
    }
}
