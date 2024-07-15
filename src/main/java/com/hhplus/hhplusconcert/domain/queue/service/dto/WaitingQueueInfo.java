package com.hhplus.hhplusconcert.domain.queue.service.dto;

import lombok.Builder;

@Builder
public record WaitingQueueInfo(Long userId, boolean isActive,
                               WaitingInfo waitingInfo) {
    public static WaitingQueueInfo of(boolean isActive, Long userId, WaitingInfo waitingInfo) {
        return WaitingQueueInfo
                .builder()
                .userId(userId)
                .isActive(isActive)
                .waitingInfo(waitingInfo)
                .build();
    }
}
