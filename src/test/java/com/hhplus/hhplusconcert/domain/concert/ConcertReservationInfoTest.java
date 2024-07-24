package com.hhplus.hhplusconcert.domain.concert;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.RESERVATION_IS_ALREADY_CANCEL;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.RESERVATION_IS_NOT_READY_TO_RESERVE;
import static com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo.ReservationStatus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConcertReservationInfoTest {

    @Test
    @DisplayName("예약을 성공한다.")
    void complete() {
        //given
        ConcertReservationInfo reservation = ConcertReservationInfo.builder()
                .status(ReservationStatus.TEMPORARY_RESERVED)
                .build();

        //when
        reservation.complete();

        //then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.COMPLETED);
    }

    @Test
    @DisplayName("예약을 완료할 수 없는 상태면 RESERVATION_IS_NOT_READY_TO_RESERVE 예외를 반환한다.")
    void completeWithNoReadyToComplete() {
        //given
        ConcertReservationInfo reservation = ConcertReservationInfo.builder()
                .status(ReservationStatus.CANCEL)
                .build();

        //when //then
        assertThatThrownBy(reservation::complete)
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(RESERVATION_IS_NOT_READY_TO_RESERVE);
    }

    @Test
    @DisplayName("예약을 취소한다.")
    void cancel() {
        //given
        ConcertReservationInfo reservation = ConcertReservationInfo.builder()
                .status(ReservationStatus.TEMPORARY_RESERVED)
                .build();

        //when
        reservation.cancel();

        //then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCEL);
    }

    @Test
    @DisplayName("이미 예약을 취소했다면 RESERVATION_IS_ALREADY_CANCEL 예외를 반환한다.")
    void cancelWithNoReadyToCancel() {
        //given
        ConcertReservationInfo reservation = ConcertReservationInfo.builder()
                .status(ReservationStatus.CANCEL)
                .build();

        //when //then
        assertThatThrownBy(reservation::cancel)
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(RESERVATION_IS_ALREADY_CANCEL);
    }

    @Test
    @DisplayName("이미 예약이 완료 상태인 경우 RESERVATION_IS_NOT_READY_TO_RESERVE 예외를 반환한다.")
    void cancelReservationWhenCompleteStatus() {
        //given
        ConcertReservationInfo concertReservationInfo = ConcertReservationInfo
                .builder()
                .status(ReservationStatus.COMPLETED)
                .build();

        // when // then
        assertThatThrownBy(concertReservationInfo::complete)
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(RESERVATION_IS_NOT_READY_TO_RESERVE);
    }

}