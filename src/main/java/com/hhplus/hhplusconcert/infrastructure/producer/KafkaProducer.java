package com.hhplus.hhplusconcert.infrastructure.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public void publish(String topic, Long outboxId, String payload) {
        log.info("sending payload={} to topic={}, at outboxId={}", payload, topic, outboxId);
        kafkaTemplate.send(topic, String.valueOf(outboxId), payload);
    }
}