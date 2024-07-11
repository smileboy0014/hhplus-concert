package com.hhplus.hhplusconcert.application.concert;

import com.hhplus.hhplusconcert.application.reservation.ReservationFacade;
import com.hhplus.hhplusconcert.domain.concert.service.ConcertService;
import com.hhplus.hhplusconcert.domain.concert.service.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConcertFacadeTest {

    @Mock
    private ConcertService concertService;

    @InjectMocks
    private ConcertFacade concertFacade;

    @InjectMocks
    private ReservationFacade reservationFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("콘서트 목록 조회 유즈케이스를 실행한다.")
    void getConcerts() {
        //given
        List<ConcertResponse> response = List.of(
                ConcertResponse.builder()
                        .concertId(1L)
                        .name("싸이 흠뻑쇼")
                        .period("20204-06-20~20204-06-21")
                        .place("서울대공원")
                        .build(),
                ConcertResponse.builder()
                        .concertId(2L)
                        .name("싸이 흠뻑쇼")
                        .period("20204-06-23~20204-06-24")
                        .place("부산 아시아드")
                        .build()
        );
        when(concertService.getConcerts()).thenReturn(response);

        //when
        List<ConcertResponse> result = concertFacade.getConcerts();

        //then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("콘서트 상세 조회 유즈케이스를 실행한다.")
    void getConcert() {
        //given
        ConcertResponse response = ConcertResponse.builder()
                .concertId(1L)
                .name("싸이 흠뻑쇼")
                .period("20204-06-20~20204-06-21")
                .place("서울대공원")
                .build();
        when(concertService.getConcert(1L)).thenReturn(response);

        //when
        ConcertResponse result = concertFacade.getConcert(1L);

        //then
        assertThat(result.name()).isEqualTo("싸이 흠뻑쇼");
    }

    @Test
    @DisplayName("예약 가능한 콘서트 날짜를 조회하는 유즈케이스를 실행한다.")
    void getConcertDates() {
        //given
        Long concertId = 1L;
        List<ConcertDateResponse> response = List.of(
                ConcertDateResponse.builder()
                        .isAvailable(true)
                        .concertDate("20204-06-20")
                        .build());

        when(concertService.getConcertDates(concertId)).thenReturn(response);

        //when
        List<ConcertDateResponse> result = concertFacade.getConcertDates(concertId);

        //then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("예약 가능한 좌석을 조회하는 유즈케이스를 실행한다.")
    void getAvailableSeats() {
        //given
        Long concertDateId = 1L;
        List<ConcertSeatResponse> response = List.of(
                ConcertSeatResponse.builder()
                        .seatNumber(2)
                        .build(),
                ConcertSeatResponse.builder()
                        .seatNumber(3)
                        .build(),
                ConcertSeatResponse.builder()
                        .seatNumber(4)
                        .build());

        when(concertService.getAvailableSeats(concertDateId)).thenReturn(response);

        //when
        List<ConcertSeatResponse> result = concertFacade.getAvailableSeats(concertDateId);

        //then
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("좌석 예약을 요청하는 유즈케이스를 실행한다.")
    void reserveSeat() {
        // given
        ReservationReserveServiceRequest request = ReservationReserveServiceRequest
                .builder()
                .concertId(1L)
                .concertDateId(1L)
                .seatNumber(11)
                .userId(1L)
                .build();

        ReservationResponse response = ReservationResponse.builder()
                .reservationId(1L)
                .build();

        when(concertService.reserveSeat(request)).thenReturn(response);

        // when
        ReservationResponse result = reservationFacade.reserveSeat(request);

        // then
        assertThat(result.reservationId()).isEqualTo(1L);

    }

    @Test
    @DisplayName("내 예약 현황을 조회하는 유즈케이스를 실행한다.")
    void getReservations() {
        // given
        Long userId = 1L;
        List<ReservationResponse> response = List.of(
                ReservationResponse
                        .builder()
                        .reservationId(1L)
                        .build());

        when(concertService.getReservations(userId)).thenReturn(response);

        // when
        List<ReservationResponse> result = reservationFacade.getReservations(userId);

        // then
        assertThat(result).hasSize(1);

    }

    @Test
    @DisplayName("에약 취소를 요청하는 유즈케이스를 실행한다.")
    void cancelReservation() {
        // given
        Long reservationId = 1L;

        // when
        reservationFacade.cancelReservation(reservationId);

        // then
        verify(concertService).cancelReservation(reservationId);
    }
}