package com.hhplus.hhplusconcert.domain.queue.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum WaitingQueueEnums {

    ACTIVE_USER_COUNT(50), //최대 활성화 유저 수
    AUTO_EXPIRED_TIME(5 * 60 * 1000); // 대기열 자동 만료 시간 (5분)

    private final int info;
}
