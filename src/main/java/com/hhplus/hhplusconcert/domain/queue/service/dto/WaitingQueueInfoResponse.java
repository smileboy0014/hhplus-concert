package com.hhplus.hhplusconcert.domain.queue.service.dto;

import lombok.Builder;

@Builder
public record WaitingQueueInfoResponse(long waitingNumber, long expectedWaitingTime) {
    public static WaitingQueueInfoResponse of(long waitingNumber, long expectedWaitingTime) {
        return WaitingQueueInfoResponse
                .builder()
                .waitingNumber(waitingNumber)
                .expectedWaitingTime(expectedWaitingTime)
                .build();
    }
}
