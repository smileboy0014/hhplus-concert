package com.hhplus.hhplusconcert.presentation.user.dto;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UserBalanceRequest(@Positive(message = "0 이상의 포인트를 충전 가능합니다.") BigDecimal balance) {
}
