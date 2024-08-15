package com.hhplus.hhplusconcert.domain.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // Concert 관련 ErrorCode
    CONCERT_IS_NOT_FOUND("콘서트 정보가 존재하지 않습니다."),
    CONCERT_DATE_IS_NOT_FOUND("예약 가능한 날짜가 존재하지 않습니다."),


    // Reservation 관련 ErrorCode
    RESERVATION_IS_FAILED("예약에 실패하였습니다."),
    RESERVATION_IS_NOT_READY_TO_RESERVE("예약 완료할 수 있는 상태가 아닙니다"),
    RESERVATION_IS_ALREADY_EXISTED("이미 해당 좌석의 예약 내역이 존재합니다."),
    RESERVATION_IS_NOT_FOUND("해당 예약 내역이 존재하지 않습니다."),
    RESERVATION_IS_ALREADY_CANCEL("이미 예약이 취소되었습니다."),
    RESERVATION_IS_ALREADY_CANCEL_OR_COMPLETE("이미 완료되었거나 취소된 예약입니다."),

    // Seat 관련 ErrorCode
    SEAT_IS_NOT_FOUND("좌석 정보가 존재하지 않습니다."),
    SEAT_IS_UNAVAILABLE("예약 가능한 좌석 상태가 아닙니다."),
    SEAT_IS_ALREADY_RESERVE("이미 예약된 좌석입니다."),
    SEAT_CAN_RESERVE("예약 가능한 좌석입니다."),

    // User 관련 ErrorCode
    USER_IS_NOT_FOUND("유저 정보가 존재하지 않습니다."),
    USER_CHARGE_AMOUNT_IS_NEGATIVE("0 이상의 포인트를 충전 가능합니다."),
    USER_NOT_ENOUGH_BALANCE("잔액이 충분하지 않습니다."),
    USER_FAIL_TO_USE_POINT("포인트 사용에 실패하였습니다."),
    USER_FAIL_TO_CHARGE("잔액 충전에 실패하였습니다."),
    USER_ALREADY_CHARGE_BALANCE("이미 잔액이 충전되었습니다."),

    // Payment 관련 ErrorCode
    PAYMENT_IS_FAILED("결제 내역 생성에 실패하였습니다"),
    PAYMENT_IS_NOT_FOUND("결제 내역이 존재하지 않습니다."),
    PAYMENT_IS_NOT_AVAILABLE_STATEMENT("결제 가능한 상태가 아닙니다"),
    PAYMENT_ALREADY_CANCEL_OR_REFUND("이미 취소됐거나 환불 처리된 결제 정보입니다."),
    PAYMENT_ALREADY_COMPLETE("이미 결제된 정보입니다."),

    // WaitingQueue 관련 ErrorCode

    // Jwk Token 관련 ErrorCode
    TOKEN_IS_FAILED("토큰 정보 생성에 실패하였습니다."),
    TOKEN_IS_NOT_FOUND("토큰이 존재하지 않습니다."),
    TOKEN_ACTIVE_IS_NOT_EXIST("활성화 시킬 토큰이 존재하지 않습니다"),
    TOKEN_IS_EXPIRED("토큰이 만료되었습니다."),
    INVALID_TOKEN("토큰이 유효하지 않습니다."),
    INVALID_TOKEN_PAYLOAD("USER ID 형식이 올바르지 않습니다."),
    NOT_EXIST_IN_WAITING_QUEUE("대기열에 토큰이 존재하지 않습니다."),
    TOKEN_IS_NOT_ACTIVE("아직 활성화 되지 않은 토큰입니다."),
    TOKEN_IS_NOT_YET("토큰 만료대상이 아닙니다."),
    ALREADY_TOKEN_IS_ACTIVE("이미 활성화된 토큰입니다."),

    //Client 관련
    DATA_PLATFORM_SEND_FAIL("예약 완료 정보를 전달하는데 실패하였습니다"),
    PUSH_KAKAOTALK_MESSAGE_FAIL("카카오톡 메세지를 전달하는데 실패하였습니다"),

    // Lock 관련
    LOCK_ACQUIRE_FAILED("Lock을 획득하는데 실패하였습니다"),

    // Outbox 관련
    OUTBOX_IS_FAILED("outbox 데이터 생성에 실패하였습니다."),
    OUTBOX_IS_NOT_FOUND("outbox 데이터가 존재하지 않습니다."),
    OUTBOX_IS_ALREADY_DONE("이미 완료된 outbox 데이터 입니다."),
    OUTBOX_IS_ALREADY_FAIL("이미 실패한 outbox 데이터 입니다."),
    OUTBOX_IS_NOT_INIT_STATUS("outbox 데이터가 초기 상태가 아닙니다."),

    // kafka 관련
    KAFKA_PUBLISH_FAILED("kafka 메시지 발행에 실패하였습니다");


    private final String msg;
}
