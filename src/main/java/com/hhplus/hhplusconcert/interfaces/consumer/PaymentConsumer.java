package com.hhplus.hhplusconcert.interfaces.consumer;

import com.hhplus.hhplusconcert.domain.payment.client.DataPlatformClient;
import com.hhplus.hhplusconcert.domain.payment.client.PushClient;
import com.hhplus.hhplusconcert.domain.payment.event.PaymentEvent;
import com.hhplus.hhplusconcert.support.utils.JsonUtils;
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

    @KafkaListener(topics = PAYMENT_TOPIC, groupId = "hhplus-01")
    public void sendPaymentInfo(String key, String message) {
        log.info("[KAFKA] :: CONSUMER:: Received PAYMENT_TOPIC, key: {}, payload: {}", key, message);

        PaymentEvent payload = JsonUtils.toObject(message, PaymentEvent.class);
        // 결제 정보 전달
        dataPlatformClient.sendPaymentResult(payload.getPayment());
        // kakaotalk 알람 전달
        pushClient.pushKakaotalk();
    }
}
