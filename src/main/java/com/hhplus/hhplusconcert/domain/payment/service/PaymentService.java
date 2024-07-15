package com.hhplus.hhplusconcert.domain.payment.service;

import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import com.hhplus.hhplusconcert.domain.payment.repository.PaymentRepository;
import com.hhplus.hhplusconcert.domain.payment.service.dto.PayServiceRequest;
import com.hhplus.hhplusconcert.domain.payment.service.dto.PaymentInfo;
import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import com.hhplus.hhplusconcert.domain.queue.repository.WaitingQueueRepository;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final UserRepository userRepository;

    private final WaitingQueueRepository waitingQueueRepository;

    /**
     * 결제를 요청하면 결제 정보를 반환한다.
     *
     * @param request reservationId, userId 정보
     * @return PaymentResponse 결제 정보를 반환한다.
     */
    @Transactional
    public PaymentInfo pay(PayServiceRequest request) {

        // 1. 결제 상태 검증
        Payment payment = paymentRepository.findByReservationId(request.reservationId());
        payment.checkStatus();
        // 2. 잔액 확인
        User user = userRepository.findUserByUserId(request.userId());
        user.checkBalance(payment.getPrice());
        // 3. 잔액 차감
        user.useBalance(payment.getPrice());
        // 4. 예약 완료
        payment.getReservation().complete();
        // 5. 결제 완료
        payment.complete();
        // 6. 결제 정보 수정
        payment.payPrice();
        // 6. 토큰 만료
        WaitingQueue queue = waitingQueueRepository.findByUserIdAndStatusIs(request.userId(), WaitingQueueStatus.ACTIVE);
        queue.expire();

        return PaymentInfo.of(payment, user);
    }
}
