package com.hhplus.hhplusconcert.domain.queue;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WaitingQueueConstants {

    public static final String WAIT_KEY = "waiting:wait";
    public static final String ACTIVE_KEY = "waiting:active";

    public static final int MAX_ACTIVE_USER = 1000; //첫 최대활성화 열 유저 수
    public static final int ENTER_10_SECONDS = 3000; // 매 10초마다 활성화 입장 수: 3000명
    public static final long AUTO_EXPIRED_TIME = 5 * 60 * 1000; // 자동 만료 시간 : 5분
//public static final long AUTO_EXPIRED_TIME = 1 * 60 * 1000; //1분
}
