package com.hhplus.hhplusconcert.interfaces.controller.queue.dto;

import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueEnterServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record WaitingQueueEnterRequest(@NotNull Long userId,
                                       @NotBlank String token) {
    public WaitingQueueEnterServiceRequest toServiceRequest() {

        return WaitingQueueEnterServiceRequest
                .builder()
                .userId(userId)
                .token(token)
                .build();
    }
}
