package com.hhplus.hhplusconcert.presentation.queue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QueueEnterRequest(@NotNull Long userId,
                                @NotBlank String token) {
}
