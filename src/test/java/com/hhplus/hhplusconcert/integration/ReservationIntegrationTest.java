package com.hhplus.hhplusconcert.integration;

import com.hhplus.hhplusconcert.application.reservation.ReservationFacade;
import com.hhplus.hhplusconcert.domain.concert.ConcertRepository;
import com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo;
import com.hhplus.hhplusconcert.domain.concert.command.ReservationCommand;
import com.hhplus.hhplusconcert.domain.user.User;
import com.hhplus.hhplusconcert.domain.user.UserRepository;
import com.hhplus.hhplusconcert.integration.common.BaseIntegrationTest;
import com.hhplus.hhplusconcert.integration.common.TestDataHandler;
import com.hhplus.hhplusconcert.interfaces.controller.reservation.dto.ReservationDto;
import com.hhplus.hhplusconcert.support.redis.RedisSpinLockService;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.*;
import static com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo.ReservationStatus;
import static com.hhplus.hhplusconcert.interfaces.controller.reservation.ReservationController.SUCCESS_CANCEL_RESERVATION;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class ReservationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ReservationFacade reservationFacade;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private TestDataHandler testDataHandler;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    RedisSpinLockService redisSpinLockService;

    private static final String PATH = "/api/v1/reservations";


    @Test
    @DisplayName("원하는 콘서트 좌석을 예약하고, 예약 정보를 반환 받는다.")
    void reserveSeat() {
        // given
        ReservationDto.Request request = ReservationDto.Request.builder()
                .concertId(1L)
                .concertDateId(1L)
                .userId(1L)
                .seatNumber(45)
                .build();

        // when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH, request);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("data", ReservationDto.Response.class)
                    .reservationId()).isEqualTo(3L);
        });
    }

    @Test
    @DisplayName("이미 좌석 예약 내역이 있다면 msg 에 RESERVATION_IS_ALREADY_EXISTED 를 반환한다.")
    void reserveSeatAlreadyReserved() {
        //given
        ReservationDto.Request request = ReservationDto.Request.builder()
                .concertId(1L)
                .concertDateId(1L)
                .userId(1L)
                .seatNumber(31)
                .build();

        //when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH, request);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getBoolean("success")).isFalse();
            softly.assertThat(result.body().jsonPath().getObject("msg", String.class))
                    .contains(RESERVATION_IS_ALREADY_EXISTED.name());
        });
    }

    @Test
    @DisplayName("예약하고자 하는 콘서트 날짜가 존재하지 않는다면 msg 에 CONCERT_DATE_IS_NOT_FOUND 를 반환한다.")
    void reserveNotExistDate() {
        //given
        ReservationDto.Request request = ReservationDto.Request.builder()
                .concertId(1L)
                .concertDateId(2L)
                .userId(1L)
                .seatNumber(35)
                .build();

        //when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH, request);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getBoolean("success")).isFalse();
            softly.assertThat(result.body().jsonPath().getObject("msg", String.class))
                    .contains(CONCERT_DATE_IS_NOT_FOUND.name());
        });
    }

    @Test
    @DisplayName("예약하고자 하는 좌석이 존재하지 않는다면 msg 에 SEAT_IS_NOT_FOUND 를 반환한다.")
    void reserveNotExistSeat() {
        //given
        ReservationDto.Request request = ReservationDto.Request.builder()
                .concertId(1L)
                .concertDateId(1L)
                .userId(1L)
                .seatNumber(100)
                .build();

        //when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH, request);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getBoolean("success")).isFalse();
            softly.assertThat(result.body().jsonPath().getObject("msg", String.class))
                    .contains(SEAT_IS_NOT_FOUND.name());
        });
    }

    @Test
    @DisplayName("내가 예약한 예약 내역을 가져온다.")
    void getMyReservations() {
        //given
        long userId = 1L;

        //when
        ExtractableResponse<Response> result = get(LOCAL_HOST + port + PATH + "/" + userId);

        //then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getList("data").size()).isEqualTo(2);
        });
    }

    @Test
    @DisplayName("예약을 취소하면 취소 성공 메시지를 반환한다.")
    void cancelReservation() {
        //given
        long reservationId = 1L;
        testDataHandler.settingUser(BigDecimal.ZERO);
        testDataHandler.reserveSeat(1L, 46);


        // when
        ExtractableResponse<Response> result = delete(LOCAL_HOST + port + PATH + "/" + reservationId);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("data", String.class))
                    .isEqualTo(SUCCESS_CANCEL_RESERVATION);
        });
    }

    @Test
    @DisplayName("취소할 예약 내역이 없으면 msg 에 RESERVATION_IS_NOT_FOUND 를 반환한다.")
    void cancelReservationWithNoHistory() {
        // given
        long reservationId = 100000L;

        // when
        ExtractableResponse<Response> result = delete(LOCAL_HOST + port + PATH + "/" + reservationId);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getBoolean("success")).isFalse();
            softly.assertThat(result.body().jsonPath().getObject("msg", String.class))
                    .contains(RESERVATION_IS_NOT_FOUND.name());
        });
    }

    @Test
    @DisplayName("1000명의 유저가 동시에 예약 신청을 하면 한 명만 예약에 성공하고, 나머지는 예외를 반환한다.")
    void reserveSeatWhenConcurrency1000EnvWithLock() throws InterruptedException {
        //given
        concertRepository.deleteAllReservation();

        int numThreads = 1000;
        int expectSuccessCnt = 1;
        int expectFailCnt = 999;
        for (int i = 0; i < 1000; i++) testDataHandler.settingUser(BigDecimal.ZERO);
        List<User> users = userRepository.getUsers();
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
                    ReservationCommand.Create command = new ReservationCommand.Create(1L, 1L,
                            49, userIds.poll());
                    reservationFacade.reserveSeat(command);
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

        List<ConcertReservationInfo> result = concertRepository.getReservations();

        // then
        assertSoftly(softly -> {
            softly.assertThat(result).hasSize(1);
            softly.assertThat(result.get(0).getStatus()).isEqualTo(ReservationStatus.TEMPORARY_RESERVED);
            softly.assertThat(successCount.get()).isEqualTo(expectSuccessCnt);
            softly.assertThat(failCount.get()).isEqualTo(expectFailCnt);
        });
    }
}


