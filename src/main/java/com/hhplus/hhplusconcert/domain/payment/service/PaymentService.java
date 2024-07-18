package com.hhplus.hhplusconcert.domain.payment.service;

import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import com.hhplus.hhplusconcert.domain.payment.service.dto.PayServiceRequest;
import com.hhplus.hhplusconcert.domain.payment.service.dto.PaymentInfo;
import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import com.hhplus.hhplusconcert.domain.queue.service.WaitingQueueFinder;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.service.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentFinder paymentFinder;
    private final PaymentReader paymentReader;
    private final UserFinder userFinder;
    private final WaitingQueueFinder waitingQueueFinder;


    /**
     * 결제를 요청하면 결제 정보를 반환한다.
     *
     * @param request reservationId, userId 정보
     * @return PaymentResponse 결제 정보를 반환한다.
     */
    @Transactional
    public PaymentInfo pay(PayServiceRequest request) {

        // 1. 결제 상태 검증
        Payment payment = paymentFinder.findPaymentByReservationId(request.reservationId());
        payment.checkStatus();
        // 2. 잔액 차감
        User user = userFinder.findUserByUserIdWithLock(request.userId());
        user.useBalance(payment.getPrice());
        // 3. 예약 완료
        payment.getReservation().complete();
        // 4. 결제 완료
        payment.complete();
        // 5. 결제 정보 수정
        payment.payPrice();
        // 6. 토큰 만료
        WaitingQueue queue = waitingQueueFinder.findWaitingQueueByUserIdAndStatusIs(request.userId(), WaitingQueueStatus.ACTIVE);
        queue.expire();

        return paymentReader.readPayment(payment, user);
    }
}
