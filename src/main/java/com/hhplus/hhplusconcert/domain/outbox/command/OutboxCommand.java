package com.hhplus.hhplusconcert.domain.outbox.command;

import com.hhplus.hhplusconcert.domain.outbox.Outbox;

import static com.hhplus.hhplusconcert.domain.outbox.Outbox.DomainType;
import static com.hhplus.hhplusconcert.domain.outbox.Outbox.EventStatus;

public class OutboxCommand {
    public record Create(
            String messageId,
            DomainType type,
            EventStatus status,
            String payload) {

        public Outbox toDomain() {
            return Outbox.builder()
                    .messageId(messageId)
                    .type(type)
                    .status(status)
                    .payload(payload)
                    .build();
        }
    }
}
