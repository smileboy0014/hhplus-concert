package com.hhplus.hhplusconcert.domain.queue.service.dto;

import lombok.Builder;

@Builder
public record WaitingQueueTokenInfo(String token) {
    public static WaitingQueueTokenInfo of(String token) {

        return WaitingQueueTokenInfo
                .builder()
                .token(token)
                .build();
    }
}
