package com.hhplus.hhplusconcert.domain.queue.entity;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.common.exception.ErrorCode;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    private WaitingQueueStatus status; // 대기 / 활성 / 만료

    private LocalDateTime requestTime; // 토큰 요청 시각

    private LocalDateTime activeTime; // 토큰 활성화 시각

    public void expireOver10min() {
        if (requestTime.isBefore(LocalDateTime.now().plusMinutes(10))
                || status == WaitingQueueStatus.ACTIVE) { //10분뒤에 만료
            throw new CustomException(ErrorCode.NOT_AVAILABLE_STATE_PAYMENT,
                    "토큰 만료대상이아닙니다.");
        }
        expire();
    }

    public void expire() {
        status = WaitingQueueStatus.EXPIRED;
    }

    public void active() {
        status = WaitingQueueStatus.ACTIVE;
        activeTime = LocalDateTime.now();
    }


}
