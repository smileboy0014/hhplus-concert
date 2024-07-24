package com.hhplus.hhplusconcert.domain.concert;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.concert.Seat.SeatStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.SEAT_CAN_RESERVE;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.SEAT_IS_UNAVAILABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SeatTest {

    @Test
    @DisplayName("좌석을 예약한다.")
    void occupySeat() {
        //given
        Seat seat = Seat.builder()
                .status(SeatStatus.AVAILABLE)
                .build();

        //when
        seat.occupy();

        //then
        assertThat(seat.getStatus()).isEqualTo(SeatStatus.UNAVAILABLE);
    }

    @Test
    @DisplayName("이미 좌석이 예약된 경우 SEAT_IS_UNAVAILABLE 예외를 반환한다.")
    void occupySeatWhenAlreadyReserved() {
        //given
        Seat seat = Seat.builder()
                .status(SeatStatus.UNAVAILABLE)
                .build();

        // when // then
        assertThatThrownBy(seat::occupy)
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(SEAT_IS_UNAVAILABLE);
    }

    @Test
    @DisplayName("좌석 예약을 취소한다.")
    void cancelReservationSeat() {
        //given
        Seat seat = Seat.builder()
                .status(SeatStatus.UNAVAILABLE)
                .build();

        //when
        seat.cancel();

        //then
        assertThat(seat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
    }

    @Test
    @DisplayName("이미 좌석 예약이 취소된 경우 SEAT_CAN_RESERVE 예외를 반환한다.")
    void cancelReservationSeatWhenAlreadyCancel() {
        //given
        Seat seat = Seat.builder()
                .status(SeatStatus.AVAILABLE)
                .build();

        // when // then
        assertThatThrownBy(seat::cancel)
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(SEAT_CAN_RESERVE);
    }
}