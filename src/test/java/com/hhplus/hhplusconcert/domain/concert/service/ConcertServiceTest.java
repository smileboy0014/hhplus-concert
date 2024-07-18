package com.hhplus.hhplusconcert.domain.concert.service;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.concert.entity.*;
import com.hhplus.hhplusconcert.domain.concert.enums.ReservationStatus;
import com.hhplus.hhplusconcert.domain.concert.enums.SeatStatus;
import com.hhplus.hhplusconcert.domain.concert.enums.TicketClass;
import com.hhplus.hhplusconcert.domain.concert.service.dto.*;
import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import com.hhplus.hhplusconcert.domain.payment.enums.PaymentStatus;
import com.hhplus.hhplusconcert.domain.payment.service.PaymentAppender;
import com.hhplus.hhplusconcert.domain.payment.service.PaymentFinder;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.service.UserFinder;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class ConcertServiceTest {

    @Mock
    private ConcertAppender concertAppender;
    @Mock
    private ConcertFinder concertFinder;
    @Mock
    private ConcertReader concertReader;
    @Mock
    private PaymentAppender paymentAppender;
    @Mock
    private PaymentFinder paymentFinder;
    @Mock
    private UserFinder userFinder;
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
        Concert concert1 = Concert.builder()
                .name("싸이 흠뻑쇼")
                .build();

        Concert concert2 = Concert.builder()
                .name("god 콘서트")
                .build();

        List<Concert> concerts = List.of(concert1, concert2);

        List<ConcertDate> concertDates = List.of(ConcertDate.builder()
                .placeInfo(Place.builder()
                        .name("서울대공원")
                        .build())
                .concertDate("2024-06-25")
                .build());

        ConcertInfo info1 = ConcertInfo.of(concert1, concertDates);
        ConcertInfo info2 = ConcertInfo.of(concert2, concertDates);

        when(concertFinder.findConcerts()).thenReturn(concerts);
        when(concertReader.readConcerts(concerts)).thenReturn(List.of(info1, info2));

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

        when(concertFinder.findConcerts()).thenReturn(concerts);

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
        Concert concert = Concert.builder()
                .concertId(concertId)
                .name("싸이 흠뻑쇼")
                .build();

        ConcertDate concertDate = ConcertDate.builder()
                .placeInfo(Place.builder()
                        .name("서울대공원")
                        .build())
                .concertDate("2024-06-25")
                .build();

        ConcertInfo info = ConcertInfo.of(concert, List.of(concertDate));

        when(concertFinder.findConcertByConcertId(concertId)).thenReturn(concert);
        when(concertFinder.findAllConcertDateByConcertId(concertId)).thenReturn(List.of(concertDate));
        when(concertReader.readConcert(concert, List.of(concertDate))).thenReturn(info);

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

        when(concertFinder.findConcertByConcertId(concertId))
                .thenThrow(new CustomException(CONCERT_IS_NOT_FOUND, CONCERT_IS_NOT_FOUND.getMsg()));

        //when //then
        assertThatThrownBy(() -> concertService.getConcert(concertId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CONCERT_IS_NOT_FOUND);
    }

    @Test
    @DisplayName("예약 가능한 콘서트 날짜를 반환한다.")
    void getConcertDates() {
        //given
        Long concertId = 1L;
        ConcertDate concertDate = ConcertDate.builder()
                .placeInfo(Place.builder()
                        .name("서울대공원")
                        .build())
                .concertDate("2024-06-25")
                .build();

        ConcertDateInfo info = ConcertDateInfo.builder().build();

        when(concertFinder.findAllConcertDateByConcertId(concertId)).thenReturn(List.of(concertDate));
        when(concertReader.readConcertDates(List.of(concertDate))).thenReturn(List.of(info));

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

        when(concertFinder.findAllConcertDateByConcertId(concertId))
                .thenThrow(new CustomException(AVAILABLE_DATE_IS_NOT_FOUND,
                        AVAILABLE_DATE_IS_NOT_FOUND.getMsg()));

        //when //then
        assertThatThrownBy(() -> concertService.getConcertDates(concertId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(AVAILABLE_DATE_IS_NOT_FOUND);
    }

    @Test
    @DisplayName("예약 가능한 좌석을 반환한다.")
    void getAvailableSeats() {
        //given
        Long concertDateId = 1L;
        Seat seat = Seat.builder()
                .seatId(1L)
                .seatNumber(1)
                .build();
        List<Seat> seats = List.of(seat);

        ConcertSeatInfo info = ConcertSeatInfo.of(seat);

        when(concertFinder.findAllSeatByConcertDateIdAndStatus(concertDateId, SeatStatus.AVAILABLE))
                .thenReturn(seats);
        when(concertReader.readSeats(seats)).thenReturn(List.of(info));

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

        when(concertFinder.findAllSeatByConcertDateIdAndStatus(concertDateId, SeatStatus.AVAILABLE))
                .thenThrow(new CustomException(SEAT_IS_NOT_FOUND, SEAT_IS_NOT_FOUND.getMsg()));

        //when //then
        assertThatThrownBy(() -> concertService.getAvailableSeats(concertDateId))
                .isInstanceOf(CustomException.class)
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
                .status(SeatStatus.AVAILABLE)
                .build();

        Seat afterSeat = Seat.builder()
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

        ReservationInfo info = ReservationInfo.of(reservation,
                ReservationConcertInfo.of(concertDate, seat),
                ReservationPaymentInfo.of(payment));

        when(concertFinder.findConcertDateByConcertDateIdAndConcertId(request.concertDateId(), request.concertId()))
                .thenReturn(concertDate);
        when(concertFinder.findSeatByConcertDateIdAndSeatNumberWithLock(request.concertDateId(), request.seatNumber()))
                .thenReturn(seat);
        when(concertAppender.appendReservation(any(Reservation.class)))
                .thenReturn(reservation);
        when(paymentAppender.appendPayment(any(Payment.class)))
                .thenReturn(payment);
        when(concertReader.readReservation(reservation, concertDate, seat, payment)).thenReturn(info);

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

        doThrow(new CustomException(RESERVATION_IS_ALREADY_EXISTED,
                "이미 해당 좌석의 예약 내역이 존재합니다. [concertDateId: %d, seatNumber: %d]"
                        .formatted(1L, 10)))
                .when(concertFinder).existsReservationByConcertDateIdAndSeatNumber(request.concertDateId(),
                        request.seatNumber());

        // when // then
        assertThatThrownBy(() -> concertService.reserveSeat(request))
                .isInstanceOf(CustomException.class)
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

        when(concertFinder.findConcertDateByConcertDateIdAndConcertId(request.concertDateId(), request.concertId()))
                .thenThrow(new CustomException(AVAILABLE_DATE_IS_NOT_FOUND,
                        "예약 가능한 콘서트 날짜가 존재하지 않습니다."));

        // then
        assertThatThrownBy(() -> concertService.reserveSeat(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(AVAILABLE_DATE_IS_NOT_FOUND);
    }


    @Test
    @DisplayName("내 예약 현황을 반환한다.")
    void getReservations() {
        // given
        Long userId = 1L;
        Reservation reservation =
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
                        .build();

        List<ReservationInfo> infos = List.of(ReservationInfo.builder()
                .reservationId(reservation.getReservationId())
                .build());

        when(concertFinder.findAllReservationByUserId(userId)).thenReturn(List.of(reservation));
        when(concertReader.readReservations(List.of(reservation))).thenReturn(infos);

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

        when(concertFinder.findAllReservationByUserId(userId)).thenReturn(List.of());

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

        when(concertFinder.findReservationByReservationId(reservation.getReservationId()))
                .thenReturn(reservation);
        when(paymentFinder.findPaymentByReservationId(reservation.getReservationId()))
                .thenReturn(payment);
        when(userFinder.findUserByUserId(reservation.getUserId()))
                .thenReturn(user);
        when(concertFinder.findSeatBySeatId(reservation.getSeatId()))
                .thenReturn(seat);

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
        when(concertFinder.findReservationByReservationId(reservationId))
                .thenThrow(new CustomException(RESERVATION_IS_NOT_FOUND,
                        RESERVATION_IS_NOT_FOUND.getMsg()));

        // when // then
        assertThatThrownBy(() -> concertService.cancelReservation(reservationId))
                .isInstanceOf(CustomException.class)
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

        when(concertFinder.findReservationByReservationId(reservation.getReservationId()))
                .thenReturn(reservation);

        when(paymentFinder.findPaymentByReservationId(reservation.getReservationId())).thenThrow(
                new CustomException(PAYMENT_IS_NOT_FOUND,
                        PAYMENT_IS_NOT_FOUND.getMsg()));

        // when //then
        assertThatThrownBy(() -> concertService.cancelReservation(reservation.getReservationId()))
                .isInstanceOf(CustomException.class)
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

        when(concertFinder.findReservationByReservationId(reservation.getReservationId()))
                .thenReturn(reservation);
        when(paymentFinder.findPaymentByReservationId(reservation.getReservationId())).thenReturn(payment);
        when(concertFinder.findSeatBySeatId(reservation.getSeatId())).thenThrow(
                new CustomException(SEAT_IS_NOT_FOUND,
                        SEAT_IS_NOT_FOUND.getMsg()));

        // when //then
        assertThatThrownBy(() -> concertService.cancelReservation(reservation.getReservationId()))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(SEAT_IS_NOT_FOUND);
    }


}