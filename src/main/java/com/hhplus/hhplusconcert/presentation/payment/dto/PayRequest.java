package com.hhplus.hhplusconcert.presentation.payment.dto;

import jakarta.validation.constraints.NotNull;

public record PayRequest(@NotNull Long userId) {
}
