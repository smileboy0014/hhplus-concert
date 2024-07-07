package com.hhplus.hhplusconcert.presentation.queue.dto;

import jakarta.validation.constraints.NotNull;

public record QueueTokenRequest(@NotNull Long userId) {
}
