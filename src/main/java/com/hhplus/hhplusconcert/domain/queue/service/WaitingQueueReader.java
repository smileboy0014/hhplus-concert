package com.hhplus.hhplusconcert.domain.queue.service;

import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingInfo;
import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueInfo;
import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueTokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaitingQueueReader {

    public WaitingQueueTokenInfo readWaitingQueueToken(String token) {
        return WaitingQueueTokenInfo.of(token);
    }

    public WaitingQueueInfo readWaitingQueue(boolean isActive, Long userId,
                                             long waitingNumber, long expectedWaitTimeInSeconds) {
        return WaitingQueueInfo.of(isActive, userId,
                WaitingInfo.of(waitingNumber, expectedWaitTimeInSeconds));
    }
}
