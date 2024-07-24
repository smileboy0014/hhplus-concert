package com.hhplus.hhplusconcert.infrastructure.queue;

import com.hhplus.hhplusconcert.domain.queue.WaitingQueue;
import com.hhplus.hhplusconcert.infrastructure.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "waiting_queue")
public class WaitingQueueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long waitingQueueId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private UserEntity user;

    private String token;

    @Enumerated(EnumType.STRING)
    private WaitingQueue.WaitingQueueStatus status; // 대기 / 활성 / 만료

    private LocalDateTime requestTime; // 토큰 요청 시각

    private LocalDateTime activeTime; // 토큰 활성화 시각

    public static WaitingQueueEntity toEntity(WaitingQueue queue) {
        return WaitingQueueEntity.builder()
                .waitingQueueId(queue.getWaitingQueueId() != null ? queue.getWaitingQueueId() : null)
                .user(UserEntity.toEntity(queue.getUser()))
                .token(queue.getToken())
                .status(queue.getStatus())
                .requestTime(queue.getRequestTime())
                .activeTime(queue.getActiveTime() != null ? queue.getActiveTime() : null)
                .build();
    }

    public WaitingQueue toDomain() {
        return WaitingQueue.builder()
                .waitingQueueId(waitingQueueId)
                .user(user.toDomain())
                .token(token)
                .status(status)
                .requestTime(requestTime)
                .activeTime(activeTime)
                .build();
    }

}
