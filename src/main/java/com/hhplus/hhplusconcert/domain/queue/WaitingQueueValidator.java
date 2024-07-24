package com.hhplus.hhplusconcert.domain.queue;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.common.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WaitingQueueValidator {

    public WaitingQueue checkSavedQueue(Optional<WaitingQueue> waitingQueue) {

        if (waitingQueue.isEmpty()) {
            throw new CustomException(ErrorCode.TOKEN_IS_FAILED, "토큰 정보를 생성하는데 실패하였습니다");
        }

        return waitingQueue.get();
    }
}
