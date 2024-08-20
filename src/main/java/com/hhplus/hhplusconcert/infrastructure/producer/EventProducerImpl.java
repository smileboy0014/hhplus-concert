package com.hhplus.hhplusconcert.infrastructure.producer;

import com.hhplus.hhplusconcert.domain.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventProducerImpl implements EventProducer {

    private final KafkaProducer kafkaProducer;

    @Override
    public void publish(String topic, String key, String payload) {
        kafkaProducer.publish(topic, key, payload);
    }
}
