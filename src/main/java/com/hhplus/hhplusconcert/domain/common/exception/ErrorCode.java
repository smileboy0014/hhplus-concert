package com.hhplus.hhplusconcert.domain.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // Concert 관련 ErrorCode
    CONCERT_IS_NOT_FOUND("콘서트 정보가 존재하지 않습니다."),
    AVAILABLE_DATE_IS_NOT_FOUND("예약 가능한 날짜가 존재하지 않습니다."),
    SEAT_IS_NOT_FOUND("좌석 정보가 존재하지 않습니다."),

    // Reservation 관련 ErrorCode
    RESERVATION_IS_NOT_READY_TO_RESERVE("예약 완료할 수 있는 상태가 아닙니다"),
    RESERVATION_IS_ALREADY_EXISTED("이미 해당 좌석의 예약 내역이 존재합니다."),
    RESERVATION_IS_NOT_FOUND("해당 예약 내역이 존재하지 않습니다."),
    RESERVATION_IS_ALREADY_CANCEL("이미 예약이 취소되었습니다."),

    // Seat 관련 ErrorCode
    SEAT_IS_UNAVAILABLE("예약 가능한 좌석 상태가 아닙니다."),
    SEAT_CAN_RESERVE("이미 예약 가능한 좌석입니다."),

    // User 관련 ErrorCode
    USER_IS_NOT_FOUND("유저 정보가 존재하지 않습니다."),
    CHARGE_AMOUNT_IS_NEGATIVE("0 이상의 포인트를 충전 가능합니다."),
    NOT_ENOUGH_BALANCE("잔액이 충분하지 않습니다."),
    ALREADY_CHARGE_BALANCE("이미 잔액이 충전되었습니다."),

    // Payment 관련 ErrorCode
    PAYMENT_IS_NOT_FOUND("결제 내역이 존재하지 않습니다."),
    NOT_AVAILABLE_STATE_PAYMENT("결제 가능한 상태가 아닙니다"),
    ALREADY_CANCEL_OR_REFUND("이미 취소됐거나 환불 처리된 결제 정보입니다."),
    ALREADY_PAYMENT_COMPLETE("이미 결제된 정보입니다."),

    // WaitingQueue 관련 ErrorCode

    // Jwk Token 관련 ErrorCode
    TOKEN_IS_NOT_FOUND("토큰이 존재하지 않습니다."),
    TOKEN_IS_EXPIRED("토큰이 만료되었습니다."),
    INVALID_TOKEN("토큰이 유효하지 않습니다."),
    INVALID_TOKEN_PAYLOAD("USER ID 형식이 올바르지 않습니다."),
    NOT_EXIST_IN_WAITING_QUEUE("대기열에 토큰이 존재하지 않습니다."),
    TOKEN_IS_NOT_ACTIVE("아직 활성화 되지 않은 토큰입니다."),
    ALREADY_TOKEN_IS_ACTIVE("이미 활성화된 토큰입니다.");


    private final String msg;
}
