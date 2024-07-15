package com.hhplus.hhplusconcert.interfaces.controller.queue.dto;

import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueTokenServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record WaitingQueueTokenRequest(@NotNull Long userId) {
    public WaitingQueueTokenServiceRequest toServiceRequest() {

        return WaitingQueueTokenServiceRequest
                .builder()
                .userId(userId)
                .build();

    }
}
