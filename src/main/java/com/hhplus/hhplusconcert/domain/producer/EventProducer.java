package com.hhplus.hhplusconcert.domain.producer;

public interface EventProducer {

    void publish(String topic, String key, String payload);
}
