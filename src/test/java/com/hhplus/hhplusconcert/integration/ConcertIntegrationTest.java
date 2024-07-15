package com.hhplus.hhplusconcert.integration;

import com.hhplus.hhplusconcert.application.concert.ConcertFacade;
import com.hhplus.hhplusconcert.common.utils.DateUtils;
import com.hhplus.hhplusconcert.domain.common.exception.CustomNotFoundException;
import com.hhplus.hhplusconcert.domain.concert.entity.Concert;
import com.hhplus.hhplusconcert.domain.concert.entity.ConcertDate;
import com.hhplus.hhplusconcert.domain.concert.entity.Place;
import com.hhplus.hhplusconcert.domain.concert.entity.Seat;
import com.hhplus.hhplusconcert.domain.concert.enums.SeatStatus;
import com.hhplus.hhplusconcert.domain.concert.enums.TicketClass;
import com.hhplus.hhplusconcert.domain.concert.repository.ConcertRepository;
import com.hhplus.hhplusconcert.domain.concert.repository.PlaceRepository;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ConcertDateInfo;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ConcertInfo;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ConcertSeatInfo;
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

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class ConcertIntegrationTest {

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private ConcertFacade concertFacade;

    @BeforeEach
    void setUp() {
        int seatsCnt = 50;

        Place place = placeRepository.addPlace(Place.builder()
                .name("서울대공원")
                .totalSeat(seatsCnt)
                .build());

        Concert concert = concertRepository.addConcert(Concert.builder()
                .name("싸이 흠뻑쇼")
                .build());

        Concert concert2 = concertRepository.addConcert(Concert.builder()
                .name("GOD 콘서트")
                .build());


        List<ConcertDate> concertDates = new ArrayList<>();

        concertDates.add(ConcertDate.builder()
                .concertInfo(concert)
                .concertDate(DateUtils.getLocalDateTimeToString(LocalDateTime.of(2024, 6, 25, 13, 0)))
                .placeInfo(place)
                .build());

        concertDates.add(ConcertDate.builder()
                .concertInfo(concert)
                .concertDate(DateUtils.getLocalDateTimeToString(LocalDateTime.of(2024, 6, 26, 13, 0)))
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
                        .ticketClass(TicketClass.C)
                        .status(SeatStatus.UNAVAILABLE)
                        .build());
            } else if (i <= 30) { // B class
                seats.add(Seat.builder()
                        .seatNumber(i)
                        .price(BigDecimal.valueOf(150000))
                        .concertDateInfo(addedConcertDates.get(0))
                        .ticketClass(TicketClass.B)
                        .status(SeatStatus.UNAVAILABLE)
                        .build());
            } else if (i <= 40) { // A class
                seats.add(Seat.builder()
                        .seatNumber(i)
                        .price(BigDecimal.valueOf(170000))
                        .concertDateInfo(addedConcertDates.get(0))
                        .ticketClass(TicketClass.A)
                        .status(SeatStatus.UNAVAILABLE)
                        .build());
            } else {
                seats.add(Seat.builder()
                        .seatNumber(i)
                        .price(BigDecimal.valueOf(190000))
                        .concertDateInfo(addedConcertDates.get(0))
                        .ticketClass(TicketClass.S)
                        .status(SeatStatus.AVAILABLE)
                        .build());
            }
        }
        concertRepository.addSeats(seats);
    }

    @AfterEach
    void tearDown() {
        concertRepository.deleteAll();
        placeRepository.deleteAll();
    }

    @Test
    @DisplayName("콘서트 목록을 조회한다.")
    void getConcerts() {

        //given //when
        List<ConcertInfo> result = concertFacade.getConcerts();

        //then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("콘서트 정보가 없으면 빈 배열을 반환한다.")
    void getConcertsWithEmptyList() {
        //given
        concertRepository.deleteAll();

        //when
        List<ConcertInfo> result = concertFacade.getConcerts();

        //then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("콘서트 상세 정보를 조회한다.")
    void getConcert() {
        //given
        List<ConcertInfo> concerts = concertFacade.getConcerts();

        //when
        ConcertInfo concert = concertFacade.getConcert(concerts.get(0).concertId());

        //then
        assertThat(concert.name()).isEqualTo("싸이 흠뻑쇼");
    }

    @Test
    @DisplayName("등록되지 않는 콘서트 정보를 조회하면 CONCERT_NOT_FOUND 예외를 반환한다.")
    void getConcertWithNoConcert() {
        //given
        Long concertId = 10L;

        //when //then
        assertThatThrownBy(() -> concertFacade.getConcert(concertId))
                .isInstanceOf(CustomNotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(CONCERT_IS_NOT_FOUND);
    }

    @Test
    @DisplayName("예약 가능한 콘서트 날짜를 조회한다.")
    void getConcertDates() {
        //given
        List<ConcertInfo> concerts = concertFacade.getConcerts();

        //when
        List<ConcertDateInfo> result = concertFacade.getConcertDates(concerts.get(0).concertId());

        //then
        assertThat(result.get(0).isAvailable()).isTrue();
    }

    @Test
    @DisplayName("예정된 콘서트 날짜가 없으면, AVAILABLE_DATE_NOT_FOUND 예외를 반환한다.")
    void getConcertDatesWithNoDates() {
        //given
        List<Concert> concerts = concertRepository.findAllConcert();
        Long concertId = 0L;

        for (Concert c : concerts) {
            boolean result = concertRepository.existsConcertDateByConcertId(c.getConcertId());
            if (!result) {
                concertId = c.getConcertId();
                break;
            }
        }
        Long finalConcertId = concertId;

        //when //then

        assertThatThrownBy(() -> concertFacade.getConcertDates(finalConcertId))
                .isInstanceOf(CustomNotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(AVAILABLE_DATE_IS_NOT_FOUND);
    }

    @Test
    @DisplayName("예약 가능한 좌석을 조회한다.")
    void getAvailableSeats() {
        //given
        List<ConcertInfo> concerts = concertFacade.getConcerts();
        List<ConcertDateInfo> concertDates = concertFacade.getConcertDates(concerts.get(0).concertId());

        //when
        List<ConcertSeatInfo> result = concertFacade.getAvailableSeats(concertDates.get(0).concertDateId());

        //then
        assertThat(result).hasSize(10);
    }

    @Test
    @DisplayName("예약할 수 있는 좌석이 없다면, AVAILABLE_SEAT_NOT_FOUND 예외를 반환한다.")
    void getAvailableSeatsWithNoSeats() {
        //given
        ConcertDate concertDates = concertRepository.findAllConcertDates()
                .stream()
                .filter(concertDate -> !concertRepository.existSeatByConcertDateAndStatus(concertDate.getConcertDateId(), SeatStatus.AVAILABLE))
                .findFirst()
                .get();

        //when //then
        assertThatThrownBy(() -> concertFacade.getAvailableSeats(concertDates.getConcertDateId()))
                .isInstanceOf(CustomNotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(SEAT_IS_NOT_FOUND);

    }
}
