package com.hhplus.hhplusconcert.domain.concert.service;

import com.hhplus.hhplusconcert.domain.common.exception.CustomBadRequestException;
import com.hhplus.hhplusconcert.domain.common.exception.CustomNotFoundException;
import com.hhplus.hhplusconcert.domain.concert.entity.*;
import com.hhplus.hhplusconcert.domain.concert.enums.ReservationStatus;
import com.hhplus.hhplusconcert.domain.concert.enums.SeatStatus;
import com.hhplus.hhplusconcert.domain.concert.enums.TicketClass;
import com.hhplus.hhplusconcert.domain.concert.repository.ConcertRepository;
import com.hhplus.hhplusconcert.domain.concert.repository.ReservationRepository;
import com.hhplus.hhplusconcert.domain.concert.service.dto.*;
import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import com.hhplus.hhplusconcert.domain.payment.enums.PaymentStatus;
import com.hhplus.hhplusconcert.domain.payment.repository.PaymentRepository;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ConcertServiceTest {

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ConcertService concertService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("콘서트 목록을 반환한다.")
    void getConcerts() {
        //given
        List<Concert> concerts = List.of(Concert.builder()
                        .name("싸이 흠뻑쇼")
                        .build(),
                Concert.builder()
                        .name("god 콘서트")
                        .build());

        when(concertRepository.findAllConcert()).thenReturn(concerts);

        //when
        List<ConcertInfo> result = concertService.getConcerts();

        //then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("콘서트 정보가 없으면 빈 배열을 반환한다.")
    void getConcertsWithEmptyList() {
        //given
        List<Concert> concerts = List.of();

        when(concertRepository.findAllConcert()).thenReturn(concerts);

        //when
        List<ConcertInfo> result = concertService.getConcerts();

        //then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("콘서트 상세 정보를 반환한다.")
    void getConcert() {
        //given
        Long concertId = 1L;
        Concert concerts = Concert.builder()
                .concertId(concertId)
                .name("싸이 흠뻑쇼")
                .build();

        when(concertRepository.findConcertByConcertId(concertId)).thenReturn(concerts);

        //when
        ConcertInfo result = concertService.getConcert(concertId);

        //then
        assertThat(result.name()).isEqualTo("싸이 흠뻑쇼");
    }

    @Test
    @DisplayName("등록되지 않는 콘서트 정보를 조회하면 CONCERT_NOT_FOUND 예외를 반환한다.")
    void getConcertWithNoConcert() {
        //given
        Long concertId = 1L;

        when(concertRepository.findConcertByConcertId(concertId))
                .thenThrow(new CustomNotFoundException(CONCERT_IS_NOT_FOUND, CONCERT_IS_NOT_FOUND.getMsg()));

        //when //then
        assertThatThrownBy(() -> concertService.getConcert(concertId))
                .isInstanceOf(CustomNotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(CONCERT_IS_NOT_FOUND);
    }

    @Test
    @DisplayName("예약 가능한 콘서트 날짜를 반환한다.")
    void getConcertDates() {
        //given
        Long concertId = 1L;
        List<ConcertDate> concertDates = List.of(ConcertDate.builder()
                .placeInfo(Place.builder()
                        .name("서울대공원")
                        .build())
                .concertDate("2024-06-25")
                .build());

        when(concertRepository.findAllConcertDateByConcertId(concertId)).thenReturn(concertDates);
        when(concertRepository.existSeatByConcertDateAndStatus(concertId, SeatStatus.AVAILABLE)).thenReturn(true);

        //when
        List<ConcertDateInfo> result = concertService.getConcertDates(concertId);

        //then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("예정된 콘서트 날짜가 없으면, AVAILABLE_DATE_NOT_FOUND 예외를 반환한다.")
    void getConcertDatesWithNoDates() {
        //given
        Long concertId = 1L;

        when(concertRepository.findAllConcertDateByConcertId(concertId)).thenReturn(List.of());

        //when //then
        assertThatThrownBy(() -> concertService.getConcertDates(concertId))
                .isInstanceOf(CustomNotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(AVAILABLE_DATE_IS_NOT_FOUND);
    }

    @Test
    @DisplayName("예약 가능한 좌석을 반환한다.")
    void getAvailableSeats() {
        //given
        Long concertDateId = 1L;
        List<Seat> concertDates = List.of(Seat.builder()
                .seatId(1L)
                .seatNumber(1)
                .build());

        when(concertRepository.findAllSeatByConcertDateIdAndStatus(concertDateId, SeatStatus.AVAILABLE))
                .thenReturn(concertDates);

        //when
        List<ConcertSeatInfo> result = concertService.getAvailableSeats(concertDateId);

        //then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("예약할 수 있는 좌석이 없다면, AVAILABLE_SEAT_NOT_FOUND 예외를 반환한다.")
    void getAvailableSeatsWithNoSeats() {
        //given
        Long concertDateId = 1L;

        when(concertRepository.findAllSeatByConcertDateIdAndStatus(concertDateId, SeatStatus.AVAILABLE))
                .thenReturn(List.of());

        //when //then
        assertThatThrownBy(() -> concertService.getAvailableSeats(concertDateId))
                .isInstanceOf(CustomNotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(SEAT_IS_NOT_FOUND);


    }

    @Test
    @DisplayName("원하는 콘서트 좌석을 예약한다.")
    void reserveSeat() {
        // given
        ReservationReserveServiceRequest request = ReservationReserveServiceRequest.builder()
                .concertId(1L)
                .concertDateId(1L)
                .userId(1L)
                .seatNumber(10)
                .build();

        Seat seat = Seat.builder()
                .seatId(1L)
                .seatNumber(request.seatNumber())
                .price(BigDecimal.valueOf(100000))
                .ticketClass(TicketClass.S)
                .status(SeatStatus.UNAVAILABLE)
                .build();

        Concert concert = Concert.builder()
                .concertId(1L)
                .name("싸이 흠뻑쇼")
                .build();

        ConcertDate concertDate = ConcertDate.builder()
                .concertDateId(1L)
                .concertDate("2024-06-23")
                .concertInfo(concert)
                .build();

        Reservation reservation = Reservation.builder()
                .reservationId(1L)
                .userId(1L)
                .seatId(1L)
                .concertDateId(1L)
                .concertName(concert.getName())
                .concertDate(concertDate.getConcertDate())
                .seatNumber(seat.getSeatNumber())
                .status(ReservationStatus.TEMPORARY_RESERVED)
                .build();

        Payment payment = Payment.builder()
                .paymentId(1L)
                .price(seat.getPrice())
                .status(PaymentStatus.WAIT)
                .paidAt(null)
                .reservation(reservation)
                .build();

        when(reservationRepository.existsByConcertDateIdAndSeatNumberAndStatusIs(request.concertDateId(),
                request.seatNumber(), ReservationStatus.TEMPORARY_RESERVED)).thenReturn(false);
        when(concertRepository.findConcertDateByConcertDateIdAndConcertId(request.concertDateId(),
                request.concertId())).thenReturn(concertDate);
        when(concertRepository.findBySeatConcertDateIdAndSeatNumber(request.concertDateId(),
                request.seatNumber())).thenReturn(seat);
        when(reservationRepository.reserve(any(Reservation.class))).thenReturn(reservation);
        when(paymentRepository.createPayment(any(Payment.class))).thenReturn(payment);

        // when
        ReservationInfo result = concertService.reserveSeat(request);

        // then
        assertThat(result.status()).isEqualTo(ReservationStatus.TEMPORARY_RESERVED);

    }

    @Test
    @DisplayName("이미 좌석이 예약되어 있다면 RESERVATION_IS_ALREADY_EXISTED 예외를 반환한다.")
    void reserveSeatWithAlreadyReserved() {
        // given
        ReservationReserveServiceRequest request = ReservationReserveServiceRequest.builder()
                .concertId(1L)
                .concertDateId(1L)
                .userId(1L)
                .seatNumber(10)
                .build();

        when(reservationRepository.existsByConcertDateIdAndSeatNumberAndStatusIs(request.concertDateId(),
                request.seatNumber(), ReservationStatus.TEMPORARY_RESERVED)).thenReturn(true);

        // when // then
        assertThatThrownBy(() -> concertService.reserveSeat(request))
                .isInstanceOf(CustomBadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(RESERVATION_IS_ALREADY_EXISTED);
    }

    @Test
    @DisplayName("예약 가능한 콘서트 날짜가 존재하지 않는다면 AVAILABLE_DATE_IS_NOT_FOUND 예외를 반환한다.")
    void reserveSeatWithNoDates() {
        // given
        ReservationReserveServiceRequest request = ReservationReserveServiceRequest.builder()
                .concertId(1L)
                .concertDateId(1L)
                .userId(1L)
                .seatNumber(10)
                .build();

        // when
        when(reservationRepository.existsByConcertDateIdAndSeatNumberAndStatusIs(request.concertDateId(),
                request.seatNumber(), ReservationStatus.TEMPORARY_RESERVED)).thenReturn(false);
        when(concertRepository.findConcertDateByConcertDateIdAndConcertId(request.concertDateId(), request.concertId())).thenThrow(
                new CustomNotFoundException(AVAILABLE_DATE_IS_NOT_FOUND,
                        AVAILABLE_DATE_IS_NOT_FOUND.getMsg()));

        // then
        assertThatThrownBy(() -> concertService.reserveSeat(request))
                .isInstanceOf(CustomNotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(AVAILABLE_DATE_IS_NOT_FOUND);
    }


    @Test
    @DisplayName("내 예약 현황을 반환한다.")
    void getReservations() {
        // given
        Long userId = 1L;
        List<Reservation> responses = List.of(
                Reservation
                        .builder()
                        .reservationId(1L)
                        .concertId(1L)
                        .concertDateId(1L)
                        .seatId(1L)
                        .concertName("싸이 흠뻑쇼")
                        .concertDate("2024-06-25")
                        .seatNumber(45)
                        .status(ReservationStatus.COMPLETED)
                        .build()
        );

        Payment payment = Payment
                .builder()
                .paymentId(1L)
                .status(PaymentStatus.COMPLETE)
                .paymentPrice(BigDecimal.valueOf(200000))
                .build();

        when(reservationRepository.findAllByUserId(userId)).thenReturn(responses);
        when(paymentRepository.findByReservationId(1L)).thenReturn(payment);

        // when
        List<ReservationInfo> result = concertService.getReservations(userId);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("내 예약 현황이 없다면 빈 배열을 반환한다.")
    void getReservationsNoList() {
        // given
        Long userId = 1L;

        when(reservationRepository.findAllByUserId(userId)).thenReturn(List.of());

        // when
        List<ReservationInfo> result = concertService.getReservations(userId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("내가 예약 했던 예약을 취소 한다.")
    void cancelReservation() {
        // given
        Reservation reservation = Reservation
                .builder()
                .reservationId(1L)
                .concertId(1L)
                .concertDateId(1L)
                .seatId(1L)
                .concertName("싸이 흠뻑쇼")
                .concertDate("2024-06-25")
                .seatNumber(45)
                .status(ReservationStatus.COMPLETED)
                .build();

        Payment payment = Payment
                .builder()
                .paymentId(1L)
                .status(PaymentStatus.COMPLETE)
                .paymentPrice(BigDecimal.valueOf(200000))
                .build();

        Seat seat = Seat
                .builder()
                .seatId(1L)
                .seatNumber(45)
                .build();

        User user = User.builder().balance(BigDecimal.valueOf(300000)).build();

        when(reservationRepository.findByReservationId(reservation.getReservationId())).thenReturn(reservation);
        when(paymentRepository.findByReservationId(reservation.getReservationId())).thenReturn(payment);
        when(concertRepository.findSeatBySeatId(reservation.getSeatId())).thenReturn(seat);
        when(userRepository.findUserByUserId(reservation.getUserId())).thenReturn(user);

        // when
        concertService.cancelReservation(reservation.getReservationId());

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCEL);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUND);
        assertThat(seat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);

    }

    @Test
    @DisplayName("취소할 예약 내역이 없다면 RESERVATION_IS_NOT_FOUND 예외를 반환한다.")
    void cancelReservationWithNoReservation() {
        // given
        Long reservationId = 1L;
        when(reservationRepository.findByReservationId(reservationId))
                .thenThrow(new CustomNotFoundException(RESERVATION_IS_NOT_FOUND,
                        RESERVATION_IS_NOT_FOUND.getMsg()));

        // when // then
        assertThatThrownBy(() -> concertService.cancelReservation(reservationId))
                .isInstanceOf(CustomNotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(RESERVATION_IS_NOT_FOUND);
    }

    @Test
    @DisplayName("취소할 결제 내역이 없다면 RESERVATION_IS_NOT_FOUND 예외를 반환한다.")
    void cancelReservationWithNoPayment() {
        // given
        Reservation reservation = Reservation
                .builder()
                .reservationId(1L)
                .concertId(1L)
                .concertDateId(1L)
                .seatId(1L)
                .concertName("싸이 흠뻑쇼")
                .concertDate("2024-06-25")
                .seatNumber(45)
                .status(ReservationStatus.COMPLETED)
                .build();

        when(reservationRepository.findByReservationId(reservation.getReservationId())).thenReturn(reservation);
        when(paymentRepository.findByReservationId(reservation.getReservationId())).thenThrow(
                new CustomNotFoundException(PAYMENT_IS_NOT_FOUND,
                        PAYMENT_IS_NOT_FOUND.getMsg()));


        // when //then
        assertThatThrownBy(() -> concertService.cancelReservation(reservation.getReservationId()))
                .isInstanceOf(CustomNotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(PAYMENT_IS_NOT_FOUND);
    }

    @Test
    @DisplayName("예약 취소할 좌석이 없다면 SEAT_IS_NOT_FOUND 예외를 반환한다.")
    void cancelReservationWithNoSeat() {
        // given
        Reservation reservation = Reservation
                .builder()
                .reservationId(1L)
                .concertId(1L)
                .concertDateId(1L)
                .seatId(1L)
                .concertName("싸이 흠뻑쇼")
                .concertDate("2024-06-25")
                .seatNumber(45)
                .status(ReservationStatus.TEMPORARY_RESERVED)
                .build();

        Payment payment = Payment
                .builder()
                .paymentId(1L)
                .status(PaymentStatus.WAIT)
                .paymentPrice(BigDecimal.valueOf(200000))
                .build();

        when(reservationRepository.findByReservationId(reservation.getReservationId())).thenReturn(reservation);
        when(paymentRepository.findByReservationId(reservation.getReservationId())).thenReturn(payment);
        when(concertRepository.findSeatBySeatId(reservation.getSeatId())).thenThrow(
                new CustomNotFoundException(SEAT_IS_NOT_FOUND,
                        SEAT_IS_NOT_FOUND.getMsg()));


        // when //then
        assertThatThrownBy(() -> concertService.cancelReservation(reservation.getReservationId()))
                .isInstanceOf(CustomNotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(SEAT_IS_NOT_FOUND);
    }


}