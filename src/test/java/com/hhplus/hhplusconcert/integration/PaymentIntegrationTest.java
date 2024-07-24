package com.hhplus.hhplusconcert.integration;

import com.hhplus.hhplusconcert.application.payment.PaymentFacade;
import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.concert.entity.*;
import com.hhplus.hhplusconcert.domain.concert.enums.SeatStatus;
import com.hhplus.hhplusconcert.domain.concert.enums.TicketClass;
import com.hhplus.hhplusconcert.domain.concert.service.ConcertAppender;
import com.hhplus.hhplusconcert.domain.concert.service.ConcertFinder;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ReservationReserveServiceRequest;
import com.hhplus.hhplusconcert.domain.payment.enums.PaymentStatus;
import com.hhplus.hhplusconcert.domain.payment.service.PaymentAppender;
import com.hhplus.hhplusconcert.domain.payment.service.dto.PayServiceRequest;
import com.hhplus.hhplusconcert.domain.payment.service.dto.PaymentInfo;
import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import com.hhplus.hhplusconcert.domain.queue.service.WaitingQueueAppender;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.service.UserAppender;
import com.hhplus.hhplusconcert.domain.user.service.UserFinder;
import com.hhplus.hhplusconcert.support.utils.DateUtils;
import com.hhplus.hhplusconcert.support.utils.JwtUtils;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.NOT_AVAILABLE_STATE_PAYMENT;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.NOT_ENOUGH_BALANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class PaymentIntegrationTest {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private ConcertAppender concertAppender;
    @Autowired
    private UserAppender userAppender;
    @Autowired
    private WaitingQueueAppender waitingQueueAppender;
    @Autowired
    private ConcertFinder concertFinder;
    @Autowired
    private PaymentAppender paymentAppender;
    @Autowired
    private PaymentFacade paymentFacade;
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
        concertAppender.appendSeats(seats);

        User user1 = userAppender.appendUser(User.builder().balance(BigDecimal.valueOf(200000)).build());
        User user2 = userAppender.appendUser(User.builder().balance(BigDecimal.valueOf(1000)).build());

        String token1 = jwtUtils.createToken(user1.getUserId());
        String token2 = jwtUtils.createToken(user2.getUserId());

        WaitingQueue queue1 = WaitingQueue
                .builder()
                .user(user1)
                .token(token1)
                .status(WaitingQueueStatus.ACTIVE)
                .build();

        WaitingQueue queue2 = WaitingQueue
                .builder()
                .user(user2)
                .token(token2)
                .status(WaitingQueueStatus.ACTIVE)
                .build();

        waitingQueueAppender.appendWaitingQueue(queue1);
        waitingQueueAppender.appendWaitingQueue(queue2);

        ReservationReserveServiceRequest request1 = ReservationReserveServiceRequest
                .builder()
                .concertId(concert.getConcertId())
                .concertDateId(addedConcertDates.get(0).getConcertDateId())
                .seatNumber(45)
                .userId(user1.getUserId())
                .build();

        ReservationReserveServiceRequest request2 = ReservationReserveServiceRequest
                .builder()
                .concertId(concert.getConcertId())
                .concertDateId(addedConcertDates.get(0).getConcertDateId())
                .seatNumber(45)
                .userId(user2.getUserId())
                .build();

        Seat seat1 = concertFinder.findSeatByConcertDateIdAndSeatNumber(request1.concertDateId(), request1.seatNumber());
        seat1.occupy();

        Reservation reservation1 = concertAppender.appendReservation(request1.toReservationEntity(addedConcertDates.get(0), seat1));

        paymentAppender.appendPayment(request1.toPaymentEntity(reservation1, seat1));

        Seat seat = concertFinder.findSeatByConcertDateIdAndSeatNumber(request2.concertDateId(), request2.seatNumber());
        seat.occupy();

        Reservation reservation = concertAppender.appendReservation(request2.toReservationEntity(addedConcertDates.get(0), seat));

        paymentAppender.appendPayment(request2.toPaymentEntity(reservation, seat));
    }

    @AfterEach
    void tearDown() {
        paymentAppender.deleteAll();
        userAppender.deleteAll();
        waitingQueueAppender.deleteAll();
    }

    @Test
    @DisplayName("결제 요청을 하면 결제 완료 정보를 반환한다.")
    void pay() {
        List<User> user = userFinder.findUsers();
        List<Reservation> reservations = concertFinder.findAllReservationByUserId(user.get(0).getUserId());

        //given
        PayServiceRequest request = PayServiceRequest
                .builder()
                .reservationId(reservations.get(0).getReservationId())
                .userId(user.get(0).getUserId())
                .build();

        //when
        PaymentInfo result = paymentFacade.pay(request);

        //then
        assertThat(result.status()).isEqualTo(PaymentStatus.COMPLETE);
    }

    @Test
    @DisplayName("결제 상태가 WAIT 이 아니면 NOT_AVAILABLE_STATE_PAYMENT 예외를 반환한다.")
    void payWithNotWaitStatus() {
        //given
        List<User> user = userFinder.findUsers();
        List<Reservation> reservations = concertFinder.findAllReservationByUserId(user.get(0).getUserId());
        PayServiceRequest request = PayServiceRequest
                .builder()
                .reservationId(reservations.get(0).getReservationId())
                .userId(user.get(0).getUserId())
                .build();
        paymentFacade.pay(request);

        //when //then
        assertThatThrownBy(() -> paymentFacade.pay(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(NOT_AVAILABLE_STATE_PAYMENT);

    }

    @Test
    @DisplayName("결제 잔액이 부족하면 결제 시 NOT_ENOUGH_BALANCE 예외를 반환한다.")
    void payWithNotEnoughMoney() {
        //given
        User user = userFinder.findUsers().stream()
                .filter(u -> u.getBalance().compareTo(BigDecimal.valueOf(10000)) < 0)
                .findFirst().get();
        List<Reservation> reservations = concertFinder.findAllReservationByUserId(user.getUserId());

        PayServiceRequest request = PayServiceRequest
                .builder()
                .reservationId(reservations.get(0).getReservationId())
                .userId(user.getUserId())
                .build();

        //when //then
        assertThatThrownBy(() -> paymentFacade.pay(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(NOT_ENOUGH_BALANCE);

    }

    @Test
    @DisplayName("결제 요청을 동시에 따다다다닥 들어와도 한번만 결제가 된다.")
    void payAtTheSameTime() throws InterruptedException {
        //given
        int numThreads = 3;
        int expectSuccessCnt = 1;
        int expectFailCnt = 2;
        List<User> user = userFinder.findUsers();
        List<Reservation> reservations = concertFinder.findAllReservationByUserId(user.get(0).getUserId());

        PayServiceRequest request = PayServiceRequest
                .builder()
                .reservationId(reservations.get(0).getReservationId())
                .userId(user.get(0).getUserId())
                .build();

        CountDownLatch latch = new CountDownLatch(numThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        //when
        for (int i = 0; i < numThreads; i++) {
            executorService.execute(() -> {
                try {
                    paymentFacade.pay(request);
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

        List<User> result = userFinder.findUsers();

        //then
        assertThat(result.get(0).getBalance()).isEqualByComparingTo(BigDecimal.valueOf(10000));
        assertThat(successCount.get()).isEqualTo(expectSuccessCnt);
        assertThat(failCount.get()).isEqualTo(expectFailCnt);
    }

}
