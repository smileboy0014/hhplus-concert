package com.hhplus.hhplusconcert.domain.outbox;

import java.util.List;
import java.util.Optional;

public interface OutboxRepository {

    Optional<Outbox> getOutbox(String messageId);

    Optional<Outbox> saveOutbox(Outbox outbox);

    List<Outbox> getRetryOutboxes();

    List<Outbox> getOutboxes();
}