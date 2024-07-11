package com.hhplus.hhplusconcert.domain.queue.entity;

import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "waiting_queue")
public class WaitingQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long waitingQueueId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    private String token;

    private String status; // 대기 / 활성 / 만료

    private Timestamp requestTime; // 토큰 요청 시각

    private Timestamp activeTime; // 토큰 활성화 시각

    public void changeTokenStatus(String status) {
        this.status = status;
    }

    public void activeToken() {
        this.status = WaitingQueueStatus.ACTIVE.getStatus();
        this.activeTime = new Timestamp(System.currentTimeMillis());
    }
}
