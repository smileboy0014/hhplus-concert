package com.hhplus.hhplusconcert.integration;

import com.hhplus.hhplusconcert.domain.payment.Payment;
import com.hhplus.hhplusconcert.domain.payment.PaymentService;
import com.hhplus.hhplusconcert.infrastructure.producer.KafkaProducer;
import com.hhplus.hhplusconcert.integration.common.BaseIntegrationTest;
import com.hhplus.hhplusconcert.interfaces.consumer.PaymentConsumer;
import com.hhplus.hhplusconcert.support.config.KafkaTopicConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class KafkaIntegrationTest extends BaseIntegrationTest {

    @SpyBean
    private PaymentConsumer paymentConsumer;

    @Autowired
    private KafkaProducer producer;

    @MockBean
    private PaymentService paymentService;

    @Test
    @DisplayName("kafka producer 가 이벤트를 발행하면 kafka event consumer 가 메세지를 받아 로직을 처리한다.")
    void givenMessageWhenProducerPublishEvent() throws InterruptedException {
        //given
        String topic = KafkaTopicConfig.KafkaConstants.PAYMENT_TOPIC;
        String sendMessage = "1";
        Payment payment = new Payment();
        when(paymentService.getPayment(any(Long.class))).thenReturn(payment);

        //when
        producer.publish(topic, 1L, sendMessage);

        Thread.sleep(1000);

        //then
        verify(paymentConsumer, times(1)).sendPaymentInfo(any(String.class),any(String.class));
    }
}
