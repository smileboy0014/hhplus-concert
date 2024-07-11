package com.hhplus.hhplusconcert.domain.queue.service.dto;

import lombok.Builder;

@Builder
public record WaitingQueueTokenResponse(String token) {
    public static WaitingQueueTokenResponse of(String token) {

        return WaitingQueueTokenResponse
                .builder()
                .token(token)
                .build();
    }
}
