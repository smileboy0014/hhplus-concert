package com.hhplus.hhplusconcert.infrastructure.outbox;

import com.hhplus.hhplusconcert.domain.outbox.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OutboxJpaRepository extends JpaRepository<OutboxEntity, Long> {
    List<OutboxEntity> findByStatusIs(Outbox.EventStatus status);

    Optional<OutboxEntity> findByMessageId(String messageId);
}
