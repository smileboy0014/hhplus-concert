package com.hhplus.hhplusconcert.interfaces.consumer;

import com.hhplus.hhplusconcert.domain.payment.Payment;
import com.hhplus.hhplusconcert.domain.payment.PaymentService;
import com.hhplus.hhplusconcert.domain.payment.client.DataPlatformClient;
import com.hhplus.hhplusconcert.domain.payment.client.PushClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.hhplus.hhplusconcert.support.config.KafkaTopicConfig.KafkaConstants.PAYMENT_TOPIC;


@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentConsumer {

    private final DataPlatformClient dataPlatformClient;
    private final PushClient pushClient;
    private final PaymentService paymentService;

    @KafkaListener(topics = PAYMENT_TOPIC, groupId = "hhplus-01")
    public void sendPaymentInfo(String outboxId, String payload) {
        log.info("[KAFKA] Received PAYMENT_TOPIC, outBoxId: {}, payload: {}", outboxId, payload);

        Payment payment = paymentService.getPayment(Long.valueOf(payload));
        // 결제 정보 전달
        dataPlatformClient.sendPaymentResult(payment);
        // kakaotalk 알람 전달
        pushClient.pushKakaotalk();
    }
}
