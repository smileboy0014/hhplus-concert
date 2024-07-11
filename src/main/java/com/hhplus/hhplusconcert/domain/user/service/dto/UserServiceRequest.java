package com.hhplus.hhplusconcert.domain.user.service.dto;

import com.hhplus.hhplusconcert.domain.user.entity.User;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UserServiceRequest(Long userId,
                                 BigDecimal balance) {
    public User toEntity() {

        return User.builder()
                .userId(userId)
                .balance(balance)
                .build();
    }
}
