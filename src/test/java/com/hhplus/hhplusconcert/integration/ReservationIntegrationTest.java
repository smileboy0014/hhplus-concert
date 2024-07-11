package com.hhplus.hhplusconcert.integration;

import com.hhplus.hhplusconcert.application.concert.ConcertFacade;
import com.hhplus.hhplusconcert.application.reservation.ReservationFacade;
import com.hhplus.hhplusconcert.common.utils.DateUtils;
import com.hhplus.hhplusconcert.domain.common.exception.CustomNotFoundException;
import com.hhplus.hhplusconcert.domain.concert.entity.Concert;
import com.hhplus.hhplusconcert.domain.concert.entity.ConcertDate;
import com.hhplus.hhplusconcert.domain.concert.entity.Place;
import com.hhplus.hhplusconcert.domain.concert.entity.Seat;
import com.hhplus.hhplusconcert.domain.concert.enums.ReservationStatus;
import com.hhplus.hhplusconcert.domain.concert.enums.SeatStatus;
import com.hhplus.hhplusconcert.domain.concert.enums.TicketClass;
import com.hhplus.hhplusconcert.domain.concert.repository.ConcertRepository;
import com.hhplus.hhplusconcert.domain.concert.repository.PlaceRepository;
import com.hhplus.hhplusconcert.domain.concert.repository.ReservationRepository;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ReservationReserveServiceRequest;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ReservationResponse;
import com.hhplus.hhplusconcert.domain.payment.enums.PaymentStatus;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.RESERVATION_IS_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class ReservationIntegrationTest {

    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ConcertRepository concertRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReservationFacade reservationFacade;

    @BeforeEach
    void setUp() {
        int seatsCnt = 50;

        Place place = placeRepository.addPlace(Place.builder()
                .name("서울대공원")
                .totalSeat(seatsCnt)
                .build());

        Concert concert = concertRepository.addConcert(Concert.builder()
                .name("싸이 흠뻑쇼")
                .place(place)
                .build());

        List<ConcertDate> concertDates = new ArrayList<>();
        concertDates.add(ConcertDate.builder()
                .concertInfo(concert)
                .concertDate(DateUtils.getLocalDateTimeToString(LocalDateTime.of(2024, 6, 25, 13, 0)))
                .placeInfo(place)
                .build());

        List<ConcertDate> addedConcertDates = concertRepository.addConcertDates(concertDates);

        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= seatsCnt; i++) {
            if (i <= 20) { // C class
                seats.add(Seat.builder()
                        .seatNumber(i)
                        .price(BigDecimal.valueOf(120000))
                        .concertDateInfo(addedConcertDates.get(0))
                        .ticketClass(TicketClass.C.getDegree())
                        .status(SeatStatus.UNAVAILABLE.getStatus())
                        .build());
            } else if (i <= 30) { // B class
                seats.add(Seat.builder()
                        .seatNumber(i)
                        .price(BigDecimal.valueOf(150000))
                        .concertDateInfo(addedConcertDates.get(0))
                        .ticketClass(TicketClass.B.getDegree())
                        .status(SeatStatus.UNAVAILABLE.getStatus())
                        .build());
            } else if (i <= 40) { // A class
                seats.add(Seat.builder()
                        .seatNumber(i)
                        .price(BigDecimal.valueOf(170000))
                        .concertDateInfo(addedConcertDates.get(0))
                        .ticketClass(TicketClass.A.getDegree())
                        .status(SeatStatus.UNAVAILABLE.getStatus())
                        .build());
            } else {
                seats.add(Seat.builder()
                        .seatNumber(i)
                        .price(BigDecimal.valueOf(190000))
                        .concertDateInfo(addedConcertDates.get(0))
                        .ticketClass(TicketClass.S.getDegree())
                        .status(SeatStatus.AVAILABLE.getStatus())
                        .build());
            }
        }
        concertRepository.addSeats(seats);

        userRepository.addUser(User.builder().balance(BigDecimal.valueOf(50000)).build());
    }

    @AfterEach
    void tearDown() {
        concertRepository.deleteAll();
        placeRepository.deleteAll();
        reservationRepository.deleteAll();
    }

    @Test
    @DisplayName("원하는 콘서트 좌석을 예약하고, 예약 정보를 반환 받는다.")
    void reserveSeat() {
        // given
        List<Concert> concerts = concertRepository.findAllConcert();
        User user = userRepository.findAll().get(0);
        List<ConcertDate> concertDates = concertRepository.findAllConcertDateByConcertId(concerts.get(0).getConcertId());

        ReservationReserveServiceRequest request = ReservationReserveServiceRequest
                .builder()
                .concertId(concerts.get(0).getConcertId())
                .concertDateId(concertDates.get(0).getConcertDateId())
                .seatNumber(45)
                .userId(user.getUserId())
                .build();

        // when
        ReservationResponse result = reservationFacade.reserveSeat(request);

        // then
        assertThat(result.status()).isEqualTo(ReservationStatus.PROGRESSING.getStatus());
    }

    @Test
    @DisplayName("내가 예약한 콘서트 예약 현황을 반환 받는다.")
    void getReservations() {
        // given
        List<Concert> concerts = concertRepository.findAllConcert();
        User user = userRepository.findAll().get(0);
        List<ConcertDate> concertDates = concertRepository.findAllConcertDateByConcertId(concerts.get(0).getConcertId());

        ReservationReserveServiceRequest request = ReservationReserveServiceRequest
                .builder()
                .concertId(concerts.get(0).getConcertId())
                .concertDateId(concertDates.get(0).getConcertDateId())
                .seatNumber(45)
                .userId(user.getUserId())
                .build();

        reservationFacade.reserveSeat(request);

        // when
        List<ReservationResponse> result = reservationFacade.getReservations(user.getUserId());

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("내 예약 현황이 없다면 빈 배열을 반환한다.")
    void getReservationsNoList() {
        // given
        Long userId = 1L;

        // when
        List<ReservationResponse> result = reservationFacade.getReservations(userId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("내가 예약 했던 예약을 취소 한다.")
    void cancelReservation() {
        // given
        List<Concert> concerts = concertRepository.findAllConcert();
        User user = userRepository.findAll().get(0);
        List<ConcertDate> concertDates = concertRepository.findAllConcertDateByConcertId(concerts.get(0).getConcertId());

        ReservationReserveServiceRequest request = ReservationReserveServiceRequest
                .builder()
                .concertId(concerts.get(0).getConcertId())
                .concertDateId(concertDates.get(0).getConcertDateId())
                .seatNumber(45)
                .userId(user.getUserId())
                .build();

        ReservationResponse response = reservationFacade.reserveSeat(request);

        // when
        reservationFacade.cancelReservation(response.reservationId());
        List<ReservationResponse> result = reservationFacade.getReservations(user.getUserId());

        // then
        assertThat(result.get(0))
                .extracting("status", "paymentInfo.status")
                .containsExactly(ReservationStatus.CANCEL.getStatus(), PaymentStatus.CANCEL.getStatus());
    }

    @Test
    @DisplayName("취소할 예약 내역이 없다면 RESERVATION_IS_NOT_FOUND 예외를 반환한다.")
    void cancelReservationWithNoReservation() {
        // given
        Long reservationId = 100000L;

        // when // then
        assertThatThrownBy(() -> reservationFacade.cancelReservation(reservationId))
                .isInstanceOf(CustomNotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(RESERVATION_IS_NOT_FOUND);
    }

}
