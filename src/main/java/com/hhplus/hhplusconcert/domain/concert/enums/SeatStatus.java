package com.hhplus.hhplusconcert.domain.concert.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SeatStatus {

    AVAILABLE("available"),
    UNAVAILABLE("unavailable");

    private final String status;
}
