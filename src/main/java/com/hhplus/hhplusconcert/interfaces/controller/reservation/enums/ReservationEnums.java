package com.hhplus.hhplusconcert.interfaces.controller.reservation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReservationEnums {

    SUCCESS_CANCEL_RESERVATION("예약을 취소하는데 성공하였습니다.");

    public final String message;
}
