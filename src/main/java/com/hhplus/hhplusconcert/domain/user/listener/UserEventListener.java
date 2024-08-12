package com.hhplus.hhplusconcert.domain.user.listener;

import com.hhplus.hhplusconcert.domain.payment.event.PaymentEvent;
import com.hhplus.hhplusconcert.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserService userService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onPaymentEvent(PaymentEvent event) {
        // 잔액 차감
        userService.usePoint(event.getReservationInfo().getUserId(),
                event.getReservationInfo().getSeatPrice());
    }
}
