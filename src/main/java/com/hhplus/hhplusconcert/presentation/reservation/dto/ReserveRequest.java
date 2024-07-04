package com.hhplus.hhplusconcert.presentation.reservation.dto;

import jakarta.validation.constraints.NotNull;

public record ReserveRequest(@NotNull Long concertId,
                             @NotNull Long concertDateId,
                             @NotNull Long seatId,
                             @NotNull Long userId) {
}
