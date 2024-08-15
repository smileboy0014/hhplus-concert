package com.hhplus.hhplusconcert.infrastructure.outbox;

import com.hhplus.hhplusconcert.domain.outbox.Outbox;
import com.hhplus.hhplusconcert.infrastructure.common.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

import static com.hhplus.hhplusconcert.domain.outbox.Outbox.EventStatus;
import static com.hhplus.hhplusconcert.domain.outbox.Outbox.EventType;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "outbox")
public class OutboxEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outboxId;

    @Enumerated(EnumType.STRING)
    private EventType type;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    private String payload;

    private int retryCount;

    @CreatedDate
    private LocalDateTime createdAt;

    public static OutboxEntity toEntity(Outbox outbox) {
        return OutboxEntity.builder()
                .outboxId(outbox.getOutboxId())
                .type(outbox.getType())
                .status(outbox.getStatus())
                .payload(outbox.getPayload())
                .retryCount(outbox.getRetryCount())
                .createdAt(outbox.getCreatedAt())
                .build();
    }

    public Outbox toDomain() {
        return Outbox.builder()
                .outboxId(outboxId)
                .type(type)
                .status(status)
                .payload(payload)
                .retryCount(retryCount)
                .createdAt(createdAt)
                .build();
    }
}
