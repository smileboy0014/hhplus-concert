package com.hhplus.hhplusconcert.application;

import com.hhplus.hhplusconcert.application.concert.ConcertFacade;
import com.hhplus.hhplusconcert.application.reservation.ReservationFacade;
import com.hhplus.hhplusconcert.domain.concert.*;
import com.hhplus.hhplusconcert.domain.concert.command.CancelReservationCommand;
import com.hhplus.hhplusconcert.domain.concert.command.ReservationCommand;
import com.hhplus.hhplusconcert.domain.payment.Payment;
import com.hhplus.hhplusconcert.domain.payment.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo.ReservationStatus;
import static com.hhplus.hhplusconcert.domain.payment.Payment.PaymentStatus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConcertFacadeTest {

    @Mock
    private ConcertService concertService;
    @Mock
    private PaymentService paymentService;

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
        Page<Concert> response = new PageImpl<>(List.of(Concert.builder()
                        .concertId(1L)
                        .name("싸이 흠뻑쇼")
                        .build(),
                Concert.builder()
                        .concertId(2L)
                        .name("싸이 흠뻑쇼")
                        .build()));

        Pageable pageable = PageRequest.of(1, 10);
        when(concertService.getConcerts(pageable)).thenReturn(response);

        //when
        Page<Concert> result = concertFacade.getConcerts(pageable);

        //then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("콘서트 상세 조회 유즈케이스를 실행한다.")
    void getConcert() {
        //given
        Concert response = Concert.builder()
                .concertId(1L)
                .name("싸이 흠뻑쇼")
                .build();

        when(concertService.getConcert(1L)).thenReturn(response);

        //when
        Concert result = concertFacade.getConcert(1L);

        //then
        assertThat(result.getName()).isEqualTo("싸이 흠뻑쇼");
    }

    @Test
    @DisplayName("예약 가능한 콘서트 날짜를 조회하는 유즈케이스를 실행한다.")
    void getAvailableConcertDates() {
        //given
        Long concertId = 1L;
        List<ConcertDate> response = List.of(
                ConcertDate.builder()
                        .isAvailable(true)
                        .concertDate("20204-06-20")
                        .build());

        when(concertService.getAvailableConcertDates(concertId)).thenReturn(response);

        //when
        List<ConcertDate> result = concertFacade.getAvailableConcertDates(concertId);

        //then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("예약 가능한 좌석을 조회하는 유즈케이스를 실행한다.")
    void getAvailableSeats() {
        //given
        Long concertDateId = 1L;
        List<Seat> response = List.of(
                Seat.builder()
                        .seatNumber(2)
                        .build(),
                Seat.builder()
                        .seatNumber(3)
                        .build(),
                Seat.builder()
                        .seatNumber(4)
                        .build());

        when(concertService.getAvailableSeats(concertDateId)).thenReturn(response);

        //when
        List<Seat> result = concertFacade.getAvailableSeats(concertDateId);

        //then
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("좌석 예약을 요청하는 유즈케이스를 실행한다.")
    void reserveSeat() {
        // given
        ReservationCommand.Create command = new ReservationCommand.Create(
                1L,
                1L,
                35,
                1L);

        ConcertReservationInfo reservation = ConcertReservationInfo.builder()
                .reservationId(1L).build();


        when(concertService.reserveSeat(command))
                .thenReturn(reservation);

        // when
        ConcertReservationInfo result = reservationFacade.reserveSeat(command);

        // then
        assertThat(result.getReservationId()).isEqualTo(1L);

    }

    @Test
    @DisplayName("내 예약 현황을 조회하는 유즈케이스를 실행한다.")
    void getReservations() {
        // given
        Long userId = 1L;
        List<ConcertReservationInfo> response = List.of(
                ConcertReservationInfo.builder()
                        .reservationId(1L)
                        .build());

        when(concertService.getMyReservations(userId)).thenReturn(response);

        // when
        List<ConcertReservationInfo> result = reservationFacade.getMyReservations(userId);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("에약 취소를 요청하는 유즈케이스를 실행한다.")
    void cancelReservation() {
        // given
        ConcertReservationInfo reservation = ConcertReservationInfo.builder()
                .reservationId(1L)
                .status(ReservationStatus.CANCEL)
                .build();
        Payment payment = Payment.builder()
                .status(PaymentStatus.CANCEL)
                .build();
        when(concertService.cancelReservation(reservation.getReservationId())).thenReturn(reservation);
        when(paymentService.cancelPayment(reservation)).thenReturn(payment);

        CancelReservationCommand.Delete command = new CancelReservationCommand.Delete(1L, 1L);

        // when
        reservationFacade.cancelReservation(command);

        // then
        verify(concertService).cancelReservation(reservation.getReservationId());
        verify(paymentService).cancelPayment(reservation);

        assertSoftly(softly -> {
            softly.assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCEL);
            softly.assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCEL);
        });
    }

}