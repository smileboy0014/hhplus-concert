package com.hhplus.hhplusconcert.infrastructure.outbox;

import com.hhplus.hhplusconcert.domain.outbox.Outbox;
import com.hhplus.hhplusconcert.domain.outbox.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxJpaRepository outboxJpaRepository;

    @Override
    public Optional<Outbox> getOutbox(String messageId) {
        Optional<OutboxEntity> outboxEntity = outboxJpaRepository.findByMessageId(messageId);
        if (outboxEntity.isPresent()) {
            return outboxEntity.map(OutboxEntity::toDomain);
        }
        return Optional.empty();
    }

    @Override
    public List<Outbox> getOutboxes() {
        return outboxJpaRepository.findAll()
                .stream()
                .map(OutboxEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Outbox> saveOutbox(Outbox outbox) {
        OutboxEntity outboxEntity = outboxJpaRepository.save(OutboxEntity.toEntity(outbox));
        return Optional.of(outboxEntity.toDomain());

    }

    @Override
    public List<Outbox> getRetryOutboxes() {

        return outboxJpaRepository.findByStatusIs(Outbox.EventStatus.INIT).stream()
                .map(OutboxEntity::toDomain)
                .toList();

    }
}
