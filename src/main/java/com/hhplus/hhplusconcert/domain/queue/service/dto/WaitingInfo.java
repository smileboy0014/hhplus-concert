package com.hhplus.hhplusconcert.domain.queue.service.dto;

import lombok.Builder;

@Builder
public record WaitingInfo(long waitingNumber, long expectedWaitingTime) {
    public static WaitingInfo of(long waitingNumber, long expectedWaitingTime) {
        return WaitingInfo
                .builder()
                .waitingNumber(waitingNumber)
                .expectedWaitingTime(expectedWaitingTime)
                .build();
    }
}
