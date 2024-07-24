package com.hhplus.hhplusconcert.integration;

import com.hhplus.hhplusconcert.application.reservation.ReservationFacade;
import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.concert.entity.*;
import com.hhplus.hhplusconcert.domain.concert.enums.ReservationStatus;
import com.hhplus.hhplusconcert.domain.concert.enums.SeatStatus;
import com.hhplus.hhplusconcert.domain.concert.enums.TicketClass;
import com.hhplus.hhplusconcert.domain.concert.service.ConcertAppender;
import com.hhplus.hhplusconcert.domain.concert.service.ConcertFinder;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ReservationInfo;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ReservationReserveServiceRequest;
import com.hhplus.hhplusconcert.domain.payment.enums.PaymentStatus;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.service.UserAppender;
import com.hhplus.hhplusconcert.domain.user.service.UserFinder;
import com.hhplus.hhplusconcert.support.utils.DateUtils;
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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.RESERVATION_IS_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class ReservationIntegrationTest {


    @Autowired
    private ReservationFacade reservationFacade;
    @Autowired
    private ConcertAppender concertAppender;
    @Autowired
    private UserAppender userAppender;
    @Autowired
    private ConcertFinder concertFinder;
    @Autowired
    private UserFinder userFinder;

    @BeforeEach
    void setUp() {
        int seatsCnt = 50;

        Place place = concertAppender.appendPlace(Place.builder()
                .name("서울대공원")
                .totalSeat(seatsCnt)
                .build());

        Concert concert = concertAppender.appendConcert(Concert.builder()
                .name("싸이 흠뻑쇼")
                .build());

        List<ConcertDate> concertDates = new ArrayList<>();
        concertDates.add(ConcertDate.builder()
                .concertInfo(concert)
                .concertDate(DateUtils.getLocalDateTimeToString(LocalDateTime.of(2024, 6, 25, 13, 0)))
                .placeInfo(place)
                .build());

        List<ConcertDate> addedConcertDates = concertAppender.appendConcertDates(concertDates);

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
                        .status(SeatStatus.AVAILABLE)
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
        concertAppender.appendSeats(seats);

        for (int i = 0; i < 50; i++) {
            userAppender.appendUser(User.builder().balance(BigDecimal.valueOf(200000)).build());
        }
    }

    @AfterEach
    void tearDown() {
        concertAppender.deleteAll();
        userAppender.deleteAll();
    }

    @Test
    @DisplayName("원하는 콘서트 좌석을 예약하고, 예약 정보를 반환 받는다.")
    void reserveSeat() {
        // given
        List<Concert> concerts = concertFinder.findConcerts();
        User user = userFinder.findUsers().get(0);
        List<ConcertDate> concertDates = concertFinder.findAllConcertDateByConcertId(concerts.get(0).getConcertId());

        ReservationReserveServiceRequest request = ReservationReserveServiceRequest
                .builder()
                .concertId(concerts.get(0).getConcertId())
                .concertDateId(concertDates.get(0).getConcertDateId())
                .seatNumber(45)
                .userId(user.getUserId())
                .build();

        // when
        ReservationInfo result = reservationFacade.reserveSeat(request);

        // then
        assertThat(result.status()).isEqualTo(ReservationStatus.TEMPORARY_RESERVED);
    }

    @Test
    @DisplayName("50명의 유저가 동시에 예약 신청을 하면 한 명만 예약에 성공하고, 나머지는 예외를 반환한다.")
    void reserveSeatWhenConcurrency50EnvWithLock() throws InterruptedException {
        // given
        int numThreads = 50;
        int expectSuccessCnt = 1;
        int expectFailCnt = 49;

        List<Concert> concerts = concertFinder.findConcerts();
        List<ConcertDate> concertDates = concertFinder.findAllConcertDateByConcertId(concerts.get(0).getConcertId());
        List<User> users = userFinder.findUsers();
        Queue<Long> userIds = new ConcurrentLinkedDeque<>();

        for (User user : users) {
            userIds.add(user.getUserId());
        }

        CountDownLatch latch = new CountDownLatch(numThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        //when
        for (int i = 0; i < numThreads; i++) {
            executorService.execute(() -> {
                try {
                    ReservationReserveServiceRequest request = ReservationReserveServiceRequest
                            .builder()
                            .concertId(concerts.get(0).getConcertId())
                            .concertDateId(concertDates.get(0).getConcertDateId())
                            .seatNumber(45)
                            .userId(userIds.poll())
                            .build();

                    reservationFacade.reserveSeat(request);
                    successCount.getAndIncrement();

                } catch (RuntimeException e) {
                    failCount.getAndIncrement();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        List<Reservation> result = concertFinder.findReservations();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(ReservationStatus.TEMPORARY_RESERVED);
        assertThat(successCount.get()).isEqualTo(expectSuccessCnt);
        assertThat(failCount.get()).isEqualTo(expectFailCnt);
    }

    @Test
    @DisplayName("20명의 유저가 동시에 다른 좌석을 예약하면 모두 예약에 성공한다.")
    void reserveSeatWhenConcurrency10EnvWithDifferentSeatSameTime() throws InterruptedException {
        // given
        int numThreads = 20;
        int expectSuccessCnt = 20;
        int expectFailCnt = 0;

        List<Concert> concerts = concertFinder.findConcerts();
        List<ConcertDate> concertDates = concertFinder.findAllConcertDateByConcertId(concerts.get(0).getConcertId());
        List<User> users = userFinder.findUsers();
        List<Seat> seats = concertFinder.findAllSeatByConcertDateIdAndStatus(concertDates.get(0).getConcertDateId(),
                SeatStatus.AVAILABLE);


        Queue<Long> userIds = new ConcurrentLinkedDeque<>();
        Queue<Integer> seatNumbers = new ConcurrentLinkedDeque<>();

        for (User user : users) {
            userIds.add(user.getUserId());
        }

        for (Seat seat : seats) {
            seatNumbers.add(seat.getSeatNumber());
        }

        CountDownLatch latch = new CountDownLatch(numThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        //when
        for (int i = 0; i < numThreads; i++) {
            executorService.execute(() -> {
                try {
                    ReservationReserveServiceRequest request = ReservationReserveServiceRequest
                            .builder()
                            .concertId(concerts.get(0).getConcertId())
                            .concertDateId(concertDates.get(0).getConcertDateId())
                            .seatNumber(seatNumbers.poll())
                            .userId(userIds.poll())
                            .build();

                    reservationFacade.reserveSeat(request);
                    successCount.getAndIncrement();

                } catch (RuntimeException e) {
                    failCount.getAndIncrement();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        List<Reservation> result = concertFinder.findReservations();

        // then
        assertThat(result).hasSize(20);
        assertThat(successCount.get()).isEqualTo(expectSuccessCnt);
        assertThat(failCount.get()).isEqualTo(expectFailCnt);
    }

    @Test
    @DisplayName("예약을 성공 후 5분 이내 결제하지 않으면 예약이 취소된다.")
    void reserveSeatThenAutoCancelAfter5Minutes() {
        List<Concert> concerts = concertFinder.findConcerts();
        User user = userFinder.findUsers().get(0);
        List<ConcertDate> concertDates = concertFinder.findAllConcertDateByConcertId(concerts.get(0).getConcertId());

        ReservationReserveServiceRequest request = ReservationReserveServiceRequest
                .builder()
                .concertId(concerts.get(0).getConcertId())
                .concertDateId(concertDates.get(0).getConcertDateId())
                .seatNumber(45)
                .userId(user.getUserId())
                .build();

        ReservationInfo reservationInfo = reservationFacade.reserveSeat(request);

        // when
        reservationFacade.checkOccupiedSeat(LocalDateTime.now().plusMinutes(5).plusSeconds(2)); //2초 정도 버퍼를 둠

        Reservation result = concertFinder.findReservationByReservationId(reservationInfo.reservationId());

        // then
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.CANCEL);
    }


    @Test
    @DisplayName("내가 예약한 콘서트 예약 현황을 반환 받는다.")
    void getReservations() {
        // given
        List<Concert> concerts = concertFinder.findConcerts();
        User user = userFinder.findUsers().get(0);
        List<ConcertDate> concertDates = concertFinder.findAllConcertDateByConcertId(concerts.get(0).getConcertId());

        ReservationReserveServiceRequest request = ReservationReserveServiceRequest
                .builder()
                .concertId(concerts.get(0).getConcertId())
                .concertDateId(concertDates.get(0).getConcertDateId())
                .seatNumber(45)
                .userId(user.getUserId())
                .build();

        reservationFacade.reserveSeat(request);

        // when
        List<ReservationInfo> result = reservationFacade.getReservations(user.getUserId());

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("내 예약 현황이 없다면 빈 배열을 반환한다.")
    void getReservationsNoList() {
        // given
        Long userId = 1L;

        // when
        List<ReservationInfo> result = reservationFacade.getReservations(userId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("내가 예약 했던 예약을 취소 한다.")
    void cancelReservation() {
        // given
        List<Concert> concerts = concertFinder.findConcerts();
        User user = userFinder.findUsers().get(0);
        List<ConcertDate> concertDates = concertFinder.findAllConcertDateByConcertId(concerts.get(0).getConcertId());

        ReservationReserveServiceRequest request = ReservationReserveServiceRequest
                .builder()
                .concertId(concerts.get(0).getConcertId())
                .concertDateId(concertDates.get(0).getConcertDateId())
                .seatNumber(45)
                .userId(user.getUserId())
                .build();

        ReservationInfo response = reservationFacade.reserveSeat(request);

        // when
        reservationFacade.cancelReservation(response.reservationId());
        List<ReservationInfo> result = reservationFacade.getReservations(user.getUserId());

        // then
        assertThat(result.get(0))
                .extracting("status", "paymentInfo.status")
                .containsExactly(ReservationStatus.CANCEL, PaymentStatus.CANCEL);
    }

    @Test
    @DisplayName("취소할 예약 내역이 없다면 RESERVATION_IS_NOT_FOUND 예외를 반환한다.")
    void cancelReservationWithNoReservation() {
        // given
        Long reservationId = 100000L;

        // when // then
        assertThatThrownBy(() -> reservationFacade.cancelReservation(reservationId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(RESERVATION_IS_NOT_FOUND);
    }

}
