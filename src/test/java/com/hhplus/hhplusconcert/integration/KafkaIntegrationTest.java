package com.hhplus.hhplusconcert.integration;

import com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo;
import com.hhplus.hhplusconcert.domain.payment.Payment;
import com.hhplus.hhplusconcert.domain.payment.event.PaymentEvent;
import com.hhplus.hhplusconcert.infrastructure.producer.KafkaProducer;
import com.hhplus.hhplusconcert.integration.common.BaseIntegrationTest;
import com.hhplus.hhplusconcert.interfaces.consumer.PaymentConsumer;
import com.hhplus.hhplusconcert.support.config.KafkaTopicConfig;
import com.hhplus.hhplusconcert.support.utils.JsonUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class KafkaIntegrationTest extends BaseIntegrationTest {

    @SpyBean
    private PaymentConsumer paymentConsumer;

    @Autowired
    private KafkaProducer producer;

    @Test
    @DisplayName("kafka producer 가 이벤트를 발행하면 kafka event consumer 가 메세지를 받아 로직을 처리한다.")
    void givenMessageWhenProducerPublishEvent() throws InterruptedException {
        //given
        String topic = KafkaTopicConfig.KafkaConstants.PAYMENT_TOPIC;
        ConcertReservationInfo reservationInfo = ConcertReservationInfo.builder().build();
        Payment completePayment = Payment.builder().build();
        String token = "jwt-token";

        PaymentEvent event = new PaymentEvent(this, reservationInfo, completePayment, token);

        //when
        producer.publish(topic, "1", JsonUtils.toJson(event));

        Thread.sleep(1000);

        //then
        verify(paymentConsumer, times(1)).sendPaymentInfo(any(String.class), any(String.class));
    }
}
