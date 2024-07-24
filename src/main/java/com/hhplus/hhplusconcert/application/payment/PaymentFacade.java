package com.hhplus.hhplusconcert.application.payment;


import com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo;
import com.hhplus.hhplusconcert.domain.concert.ConcertService;
import com.hhplus.hhplusconcert.domain.payment.Payment;
import com.hhplus.hhplusconcert.domain.payment.PaymentService;
import com.hhplus.hhplusconcert.domain.payment.command.PaymentCommand;
import com.hhplus.hhplusconcert.domain.queue.WaitingQueueService;
import com.hhplus.hhplusconcert.domain.user.User;
import com.hhplus.hhplusconcert.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;
    private final UserService userService;
    private final ConcertService concertService;
    private final WaitingQueueService waitingQueueService;


    /**
     * 결제 요청하는 유즈케이스를 실행한다.
     *
     * @param command reservationId, userId 정보
     * @return PaymentResponse 결제 결과를 반환한다.
     */
    @Transactional
    public Payment pay(PaymentCommand.Create command) {
        // 1. 예약 완료
        ConcertReservationInfo completeReservation = concertService.completeReservation(command);

        // 2. 결제 내역 생성
        Payment payment = paymentService.createPayment(completeReservation);

        // 3. 잔액 차감
        User user = userService.usePoint(completeReservation.getUserId(),
                completeReservation.getSeatPrice());

        // 4. 토큰 만료
        waitingQueueService.forceExpireToken(command.userId());

        return Payment.builder()
                .paymentId(payment.getPaymentId())
                .paymentPrice(payment.getPaymentPrice())
                .status(payment.getStatus())
                .balance(user.getBalance())
                .paidAt(payment.getPaidAt())
                .build();
    }
}
