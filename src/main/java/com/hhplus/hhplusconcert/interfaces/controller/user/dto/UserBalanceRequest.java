package com.hhplus.hhplusconcert.interfaces.controller.user.dto;

import com.hhplus.hhplusconcert.domain.user.service.dto.UserServiceRequest;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UserBalanceRequest(@Positive(message = "0 이상의 포인트를 충전 가능합니다.") BigDecimal balance) {
    public UserServiceRequest toServiceRequest(Long userId) {
        return UserServiceRequest
                .builder()
                .userId(userId)
                .balance(balance)
                .build();
    }
}
