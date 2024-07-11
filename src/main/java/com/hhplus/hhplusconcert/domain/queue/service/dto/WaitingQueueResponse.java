package com.hhplus.hhplusconcert.domain.queue.service.dto;

import lombok.Builder;

@Builder
public record WaitingQueueResponse(Long userId, boolean isActive,
                                   WaitingQueueInfoResponse waitingInfo) {
    public static WaitingQueueResponse of(boolean isActive, Long userId, WaitingQueueInfoResponse waitingInfo) {
        return WaitingQueueResponse
                .builder()
                .userId(userId)
                .isActive(isActive)
                .waitingInfo(waitingInfo)
                .build();
    }
}
