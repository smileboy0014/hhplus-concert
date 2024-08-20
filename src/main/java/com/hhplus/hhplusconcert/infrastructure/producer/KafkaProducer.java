package com.hhplus.hhplusconcert.infrastructure.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public void publish(String topic, String key, String payload) {
        log.info("[KAFKA] :: PUBLISH :: sending  topic={}, key={}", topic, key);

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, payload);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message [{}] sent successfully with offset [{}] to topic [{}]",
                        payload,
                        result.getRecordMetadata().offset(),
                        result.getRecordMetadata().topic());
            } else {
                log.error("Failed to send message [{}] to topic [{}] due to: {}",
                        payload,
                        topic,
                        ex.getMessage());
            }
        });
    }
}