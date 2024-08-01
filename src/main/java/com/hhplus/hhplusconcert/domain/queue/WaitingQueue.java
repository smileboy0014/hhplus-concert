package com.hhplus.hhplusconcert.domain.queue;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.ALREADY_TOKEN_IS_ACTIVE;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.TOKEN_IS_NOT_YET;
import static com.hhplus.hhplusconcert.domain.queue.WaitingQueueConstants.MAX_ACTIVE_USER;
import static java.time.LocalDateTime.now;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class WaitingQueue {
    private Long waitingQueueId;

    private User user;

    private String token;

    private WaitingQueueStatus status; // 대기 / 활성 / 만료

    private LocalDateTime requestTime; // 토큰 요청 시각

    private LocalDateTime activeTime; // 토큰 활성화 시각

    private Long waitingNum;

    private Long waitTimeInSeconds;


    public void addWaitingInfo(long waitingNum, long waitTimeInSeconds) {
        this.waitingNum = waitingNum;
        this.waitTimeInSeconds = waitTimeInSeconds;
    }


    public static long calculateActiveCnt(long activeTokenCnt) {

        return MAX_ACTIVE_USER - activeTokenCnt;
    }

    public enum WaitingQueueStatus {

        WAIT, // 대기 중
        ACTIVE, // 활성화
        EXPIRED // 만료
    }

    public static WaitingQueue toDomain(long availableActiveTokenCnt, User user, String token) {

        if (availableActiveTokenCnt > 0) return WaitingQueue.toActiveDomain(user, token);

        return WaitingQueue.toWaitingDomain(user, token);
    }

    public static WaitingQueue toActiveDomain(User user, String token) {
        return WaitingQueue.builder()
                .user(user)
                .token(token)
                .status(WaitingQueueStatus.ACTIVE)
                .requestTime(now())
                .activeTime(now())
                .build();
    }

    public static WaitingQueue toWaitingDomain(User user, String token) {
        return WaitingQueue.builder()
                .user(user)
                .token(token)
                .status(WaitingQueueStatus.WAIT)
                .requestTime(now())
                .build();
    }


    public void isActive() {
        if (status == WaitingQueueStatus.ACTIVE) {
            throw new CustomException(ALREADY_TOKEN_IS_ACTIVE, "이미 활성화 된 토큰입니다.");
        }
    }


    public void expireOver10min() {
        if (activeTime.isAfter(LocalDateTime.now().plusMinutes(10))) { //10분뒤에 만료
            throw new CustomException(TOKEN_IS_NOT_YET,
                    "토큰 만료 대상이 아닙니다.");
        }
        expire();
    }

    public void expire() {
        status = WaitingQueueStatus.EXPIRED;
    }

    public void active() {
        if (status == WaitingQueueStatus.ACTIVE) {
            throw new CustomException(ALREADY_TOKEN_IS_ACTIVE,
                    "이미 토큰이 활성화 상태입니다.");
        }
        status = WaitingQueueStatus.ACTIVE;
        activeTime = LocalDateTime.now();
    }


}
