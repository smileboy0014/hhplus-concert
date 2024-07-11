package com.hhplus.hhplusconcert.domain.user.service.dto;

import com.hhplus.hhplusconcert.domain.user.entity.User;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UserResponse(Long userId, BigDecimal balance) {
    public static UserResponse of(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .balance(user.getBalance())
                .build();
    }
}
