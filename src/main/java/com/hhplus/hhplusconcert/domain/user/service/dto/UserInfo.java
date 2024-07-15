package com.hhplus.hhplusconcert.domain.user.service.dto;

import com.hhplus.hhplusconcert.domain.user.entity.User;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UserInfo(Long userId, BigDecimal balance) {
    public static UserInfo of(User user) {
        return UserInfo.builder()
                .userId(user.getUserId())
                .balance(user.getBalance())
                .build();
    }
}
