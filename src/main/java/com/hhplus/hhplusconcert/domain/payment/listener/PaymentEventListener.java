package com.hhplus.hhplusconcert.domain.payment.listener;

import com.hhplus.hhplusconcert.domain.payment.client.DataPlatformClient;
import com.hhplus.hhplusconcert.domain.payment.client.PushClient;
import com.hhplus.hhplusconcert.domain.payment.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final DataPlatformClient dataPlatformClient;

    private final PushClient pushClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentEvent(PaymentEvent event) {
        // 결제 정보 전달
        dataPlatformClient.sendPaymentResult(event.getPayment());
        // kakaotalk 알람 전달
        pushClient.pushKakaotalk();
    }
}
