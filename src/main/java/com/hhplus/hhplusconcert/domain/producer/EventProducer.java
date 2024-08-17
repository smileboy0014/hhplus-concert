package com.hhplus.hhplusconcert.domain.producer;

public interface EventProducer {

    void publish(String topic, Long outboxId, String payload);
}
