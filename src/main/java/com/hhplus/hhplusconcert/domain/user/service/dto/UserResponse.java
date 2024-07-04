package com.hhplus.hhplusconcert.domain.user.service.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UserResponse(BigDecimal balance) {
}
