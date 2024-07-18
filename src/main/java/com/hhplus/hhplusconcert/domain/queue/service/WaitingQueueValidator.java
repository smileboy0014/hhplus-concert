package com.hhplus.hhplusconcert.domain.queue.service;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.common.exception.ErrorCode;
import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import org.springframework.stereotype.Component;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.TOKEN_IS_EXPIRED;

@Component
public class WaitingQueueValidator {

    // 토큰이 Waiting 상태인지 보장
    public void ensureIsWaiting(WaitingQueue queue) {
        // 1. 토큰이 만료된 경우
        if (queue.getStatus() == WaitingQueueStatus.EXPIRED) {
            throw new CustomException(TOKEN_IS_EXPIRED,
                    "토큰이 만료되었습니다. 다시 발급 후 대기열에 진입해주세요.");
        }
        // 2. 토큰이 활성화상태인 경우
        if (queue.getStatus() == WaitingQueueStatus.ACTIVE) {
            throw new CustomException(ErrorCode.ALREADY_TOKEN_IS_ACTIVE, "이미 활성화 된 토큰입니다.");
        }
    }
}
