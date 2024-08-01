package com.hhplus.hhplusconcert.domain.concert;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo.ReservationStatus;
import com.hhplus.hhplusconcert.domain.concert.command.ReservationCommand;
import com.hhplus.hhplusconcert.domain.payment.command.PaymentCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.*;
import static com.hhplus.hhplusconcert.domain.concert.Seat.SeatStatus.AVAILABLE;
import static com.hhplus.hhplusconcert.domain.concert.Seat.SeatStatus.UNAVAILABLE;
import static com.hhplus.hhplusconcert.domain.concert.Seat.TicketClass;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConcertServiceTest {

    @Mock
    private ConcertRepository concertRepository;
    @Mock
    private ConcertValidator concertValidator;
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

        when(concertRepository.getConcerts()).thenReturn(concerts);

        //when
        List<Concert> result = concertService.getConcerts();

        //then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("콘서트 정보가 없으면 빈 배열을 반환한다.")
    void getConcertsWithEmptyList() {
        //given
        List<Concert> concerts = List.of();

        when(concertRepository.getConcerts()).thenReturn(concerts);

        //when
        List<Concert> result = concertService.getConcerts();

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
                .concert(concert)
                .place(Place.builder()
                        .name("서울대공원")
                        .build())
                .concertDate("2024-06-25")
                .build();

        List<ConcertDate> concertDates = List.of(concertDate);

        when(concertRepository.getConcertDates(concertId)).thenReturn(concertDates);

        //when
        Concert result = concertService.getConcert(concertId);

        //then
        assertThat(result.getName()).isEqualTo("싸이 흠뻑쇼");
    }

    @Test
    @DisplayName("등록되지 않는 콘서트 정보를 조회하면 CONCERT_NOT_FOUND 예외를 반환한다.")
    void getConcertWithNoConcert() {
        //given
        Long concertId = 10000L;

        when(concertRepository.getConcertDates(concertId))
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
                .place(Place.builder()
                        .name("서울대공원")
                        .build())
                .concertDate("2024-06-25")
                .build();


        when(concertRepository.getConcertDates(concertId)).thenReturn(List.of(concertDate));

        //when
        List<ConcertDate> result = concertService.getAvailableConcertDates(concertId);

        //then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("예정된 콘서트 날짜가 없으면, AVAILABLE_DATE_NOT_FOUND 예외를 반환한다.")
    void getConcertDatesWithNoDates() {
        //given
        Long concertId = 1L;

        when(concertRepository.getConcertDates(concertId))
                .thenThrow(new CustomException(CONCERT_DATE_IS_NOT_FOUND,
                        CONCERT_DATE_IS_NOT_FOUND.getMsg()));

        //when //then
        assertThatThrownBy(() -> concertService.getAvailableConcertDates(concertId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CONCERT_DATE_IS_NOT_FOUND);
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

        when(concertRepository.getAvailableSeats(concertDateId)).thenReturn(seats);

        //when
        List<Seat> result = concertService.getAvailableSeats(concertDateId);

        //then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("예약할 수 있는 좌석이 없다면, AVAILABLE_SEAT_NOT_FOUND 예외를 반환한다.")
    void getAvailableSeatsWithNoSeats() {
        //given
        Long concertDateId = 1L;

        when(concertRepository.getAvailableSeats(concertDateId))
                .thenThrow(new CustomException(SEAT_IS_NOT_FOUND, SEAT_IS_NOT_FOUND.getMsg()));

        //when //then
        assertThatThrownBy(() -> concertService.getAvailableSeats(concertDateId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(SEAT_IS_NOT_FOUND);
    }


    @Test
    @DisplayName("이미 좌석이 예약되어 있다면 SEAT_IS_UNAVAILABLE 예외를 반환한다.")
    void reserveSeatWithAlreadyReserved() {
        // given
        Concert concert = Concert.builder()
                .concertId(1L)
                .name("싸이 흠뻑쇼")
                .build();

        ConcertDate concertDate = ConcertDate.builder()
                .concertDateId(1L)
                .concertDate("2024-06-23")
                .concert(concert)
                .build();

        Seat seat = Seat.builder()
                .seatId(1L)
                .seatNumber(45)
                .price(BigDecimal.valueOf(100000))
                .ticketClass(TicketClass.S)
                .status(UNAVAILABLE)
                .build();

        ReservationCommand.Create command = new ReservationCommand
                .Create(1L, 1L, 10, 1L);

        when(concertRepository.checkAlreadyReserved(command.concertId(), command.concertDateId(),
                command.seatNumber())).thenReturn(true);

        when(concertValidator.checkExistConcertDate(Optional.ofNullable(any()), any(Long.class))).thenReturn(concertDate);
        when(concertValidator.checkExistSeat(any(), any())).thenReturn(seat);


        // when // then
        assertThatThrownBy(() -> concertService.reserveSeat(command))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(SEAT_IS_UNAVAILABLE);
    }

    @Test
    @DisplayName("예약 가능한 콘서트 날짜가 존재하지 않는다면 CONCERT_DATE_IS_NOT_FOUND 예외를 반환한다.")
    void reserveSeatWithNoDates() {
        // given
        ReservationCommand.Create command = new ReservationCommand
                .Create(1L, 1L, 10, 1L);

        when(concertRepository.checkAlreadyReserved(command.concertId(), command.concertDateId(),
                command.seatNumber())).thenReturn(false);
        when(concertRepository.getAvailableDates(command.concertDateId(),
                command.concertId())).thenReturn(Optional.empty());
        when(concertValidator.checkExistConcertDate(any(), any()))
                .thenThrow(new CustomException(CONCERT_DATE_IS_NOT_FOUND, CONCERT_DATE_IS_NOT_FOUND.getMsg()));

        //when // then
        assertThatThrownBy(() -> concertService.reserveSeat(command))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CONCERT_DATE_IS_NOT_FOUND);
    }


    @Test
    @DisplayName("내 예약 현황을 반환한다.")
    void getReservations() {
        // given
        Long userId = 1L;

        List<ConcertReservationInfo> reservationInfos = List.of(
                ConcertReservationInfo.builder().reservationId(1L).build()
        );

        when(concertRepository.getMyReservations(userId)).thenReturn(reservationInfos);

        // when
        List<ConcertReservationInfo> result = concertService.getMyReservations(userId);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("내 예약 현황이 없다면 빈 배열을 반환한다.")
    void getReservationsNoList() {
        // given
        Long userId = 1L;

        when(concertRepository.getMyReservations(userId)).thenReturn(List.of());

        // when
        List<ConcertReservationInfo> result = concertService.getMyReservations(userId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("내가 예약 했던 예약을 취소 한다.")
    void cancelReservation() {
        // given
        Long reservationId = 1L;
        ConcertReservationInfo reservationInfo = ConcertReservationInfo.builder()
                .status(ReservationStatus.TEMPORARY_RESERVED)
                .build();

        ConcertReservationInfo reservationResult = ConcertReservationInfo.builder()
                .status(ReservationStatus.CANCEL)
                .build();

        when(concertRepository.getReservation(reservationId)).thenReturn(Optional.ofNullable(reservationInfo));
        when(concertValidator.checkExistReservation(any(), any())).thenReturn(reservationInfo);
        when(concertRepository.saveReservation(any(ConcertReservationInfo.class)))
                .thenReturn(Optional.ofNullable(reservationResult));
        when(concertValidator.checkSavedReservation(any(), any())).thenReturn(reservationInfo);

        // when
        ConcertReservationInfo result = concertService.cancelReservation(reservationId);

        // then
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.CANCEL);


    }

    @Test
    @DisplayName("취소할 예약 내역이 없다면 RESERVATION_IS_NOT_FOUND 예외를 반환한다.")
    void cancelReservationWithNoReservation() {
        // given
        Long reservationId = 1L;

        when(concertValidator.checkExistReservation(any(), any())).thenThrow(new CustomException(RESERVATION_IS_NOT_FOUND,
                RESERVATION_IS_NOT_FOUND.getMsg()));

        // when // then
        assertThatThrownBy(() -> concertService.cancelReservation(reservationId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(RESERVATION_IS_NOT_FOUND);
    }

    @Test
    @DisplayName("이미 예약이 취소된 상태라면 RESERVATION_IS_ALREADY_CANCEL 예외를 반환한다.")
    void cancelReservationWithAlreadyCanceled() {
        // given
        Long reservationId = 1L;
        ConcertReservationInfo reservationInfo = ConcertReservationInfo.builder()
                .status(ReservationStatus.CANCEL)
                .build();

        when(concertValidator.checkExistReservation(any(), any())).thenReturn(reservationInfo);
        when(concertRepository.getReservation(reservationId)).thenReturn(Optional.ofNullable(reservationInfo));

        // when // then
        assertThatThrownBy(() -> concertService.cancelReservation(reservationId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(RESERVATION_IS_ALREADY_CANCEL);
    }

    @Test
    @DisplayName("좌석 점유를 해지한다.")
    void cancelOccupiedSeat() {
        // given
        Long seatId = 1L;
        Seat seat = Seat.builder().status(UNAVAILABLE).build();

        when(concertRepository.getSeat(seatId)).thenReturn(Optional.ofNullable(seat));
        when(concertValidator.checkExistSeat(any(), any())).thenReturn(seat);

        // when
        concertService.cancelOccupiedSeat(seatId);

        //then
        verify(concertRepository).saveSeat(seat);
        assertThat(seat.getStatus()).isEqualTo(AVAILABLE);
    }

    @Test
    @DisplayName("좌석 점유 해지할 좌석이 존재하지 않는다면 SEAT_IS_NOT_FOUND 예외를 반환한다.")
    void cancelOccupiedSeatWithNoSeat() {
        // given
        Long seatId = 1L;
        when(concertValidator.checkExistSeat(any(), any()))
                .thenThrow(new CustomException(SEAT_IS_NOT_FOUND, SEAT_IS_NOT_FOUND.getMsg()));

        // when //then
        assertThatThrownBy(() -> concertService.cancelOccupiedSeat(seatId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(SEAT_IS_NOT_FOUND);
    }

    @Test
    @DisplayName("이미 좌석 점유가 해지된 상태라면 SEAT_CAN_RESERVE 예외를 반환한다.")
    void cancelOccupiedSeatWithAlreadyAvailable() {
        // given
        Long seatId = 1L;
        Seat seat = Seat.builder().status(AVAILABLE).build();


        when(concertRepository.getSeat(seatId)).thenReturn(Optional.ofNullable(seat));
        when(concertValidator.checkExistSeat(any(), any())).thenReturn(seat);

        // when //then
        assertThatThrownBy(() -> concertService.cancelOccupiedSeat(seatId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(SEAT_CAN_RESERVE);
    }

    @Test
    @DisplayName("결제 완료로 예약을 최종 완료한다.")
    void completeReservation() {
        // given
        PaymentCommand.Create command = new PaymentCommand.Create(1L, 1L, "jwt-token");

        ConcertReservationInfo reservationInfo = ConcertReservationInfo.builder()
                .status(ReservationStatus.TEMPORARY_RESERVED)
                .build();

        when(concertRepository.getReservation(command.reservationId())).thenReturn(Optional.ofNullable(reservationInfo));
        when(concertValidator.checkExistReservation(any(), any())).thenReturn(reservationInfo);
        when(concertRepository.saveReservation(any())).thenReturn(Optional.ofNullable(reservationInfo));
        when(concertValidator.checkSavedReservation(any(), any())).thenReturn(reservationInfo);

        // when
        ConcertReservationInfo result = concertService.completeReservation(command);

        // then
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.COMPLETED);
    }

    @Test
    @DisplayName("결제 완료로 예약을 최종 완료한다.")
    void completeReservationWithNoReservation() {
        // given
        PaymentCommand.Create command = new PaymentCommand.Create(1L, 1L, "jwt-token");

        ConcertReservationInfo reservationInfo = ConcertReservationInfo.builder()
                .status(ReservationStatus.TEMPORARY_RESERVED)
                .build();

        when(concertRepository.getReservation(command.reservationId())).thenReturn(Optional.ofNullable(reservationInfo));
        when(concertValidator.checkExistReservation(any(), any())).thenThrow(new CustomException(RESERVATION_IS_NOT_FOUND,
                RESERVATION_IS_NOT_FOUND.getMsg()));

        // when // then
        assertThatThrownBy(() -> concertService.completeReservation(command))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(RESERVATION_IS_NOT_FOUND);
    }
}