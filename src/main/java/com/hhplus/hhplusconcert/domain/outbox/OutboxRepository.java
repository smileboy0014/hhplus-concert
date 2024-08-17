package com.hhplus.hhplusconcert.domain.outbox;

import java.util.List;
import java.util.Optional;

public interface OutboxRepository {

    Optional<Outbox> getOutbox(Long outboxId);

    List<Outbox> getOutboxes();

    Optional<Outbox> saveOutbox(Outbox outbox);

    List<Outbox> getRetryOutboxes();
}