package com.hhplus.hhplusconcert.domain.concert.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReservationStatus {

    CANCEL("cancel"),
    PROGRESSING("progressing"),
    COMPLETED("completed");


    private final String status;
}
