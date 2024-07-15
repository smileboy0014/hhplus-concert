package com.hhplus.hhplusconcert.interfaces.controller.payment.dto;

import com.hhplus.hhplusconcert.domain.payment.service.dto.PayServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PayRequest(@NotNull Long reservationId, @NotNull Long userId) {

    public PayServiceRequest toServiceRequest() {
        return PayServiceRequest.builder()
                .reservationId(reservationId)
                .userId(userId)
                .build();
    }
}
