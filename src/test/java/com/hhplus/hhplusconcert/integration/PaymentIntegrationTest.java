package com.hhplus.hhplusconcert.integration;

import com.hhplus.hhplusconcert.application.payment.PaymentFacade;
import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.concert.ConcertRepository;
import com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo;
import com.hhplus.hhplusconcert.domain.outbox.Outbox;
import com.hhplus.hhplusconcert.domain.outbox.OutboxRepository;
import com.hhplus.hhplusconcert.domain.payment.Payment;
import com.hhplus.hhplusconcert.domain.payment.PaymentRepository;
import com.hhplus.hhplusconcert.domain.payment.command.PaymentCommand;
import com.hhplus.hhplusconcert.domain.payment.event.PaymentEvent;
import com.hhplus.hhplusconcert.domain.producer.EventProducer;
import com.hhplus.hhplusconcert.domain.queue.listener.QueueEventListener;
import com.hhplus.hhplusconcert.domain.user.User;
import com.hhplus.hhplusconcert.domain.user.UserRepository;
import com.hhplus.hhplusconcert.domain.user.UserService;
import com.hhplus.hhplusconcert.domain.user.command.UserCommand;
import com.hhplus.hhplusconcert.domain.user.listener.UserEventListener;
import com.hhplus.hhplusconcert.integration.common.BaseIntegrationTest;
import com.hhplus.hhplusconcert.integration.common.TestDataHandler;
import com.hhplus.hhplusconcert.interfaces.controller.payment.dto.PaymentDto;
import com.hhplus.hhplusconcert.interfaces.scheduler.OutboxScheduler;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.*;
import static com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo.ReservationStatus;
import static com.hhplus.hhplusconcert.domain.payment.Payment.PaymentStatus;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class PaymentIntegrationTest extends BaseIntegrationTest {

    private static final String PATH = "/api/v1/payments";

    @Autowired
    TestDataHandler testDataHandler;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private UserService userService;

    @SpyBean
    private UserEventListener userEventListener;

    @SpyBean
    private QueueEventListener queueEventListener;

    @Autowired
    private OutboxRepository outboxRepository;

    @SpyBean
    private EventProducer eventProducer;

    @Autowired
    private OutboxScheduler outboxScheduler;


    @Test
    @DisplayName("결제 요청을 하면 결제 완료 정보를 반환한다.")
    void pay() {
        //given
        testDataHandler.settingUser(BigDecimal.valueOf(300000));

        PaymentDto.Request request = PaymentDto.Request.builder()
                .reservationId(1L)
                .userId(1L)
                .token("jwt-token")
                .build();

        // when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH + "/pay", request);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("data", PaymentDto.Response.class)
                    .status()).isEqualTo(PaymentStatus.COMPLETE);
        });
    }

    @Test
    @DisplayName("예약 완료할 예약건이 없으면 msg 에 RESERVATION_IS_NOT_FOUND 예외를 반환한다.")
    void payWithNotWaitStatus() {
        //given
        testDataHandler.settingUser(BigDecimal.valueOf(300000));
        concertRepository.deleteAll();

        PaymentDto.Request request = PaymentDto.Request.builder()
                .reservationId(1L)
                .userId(1L)
                .token("jwt-token")
                .build();

        // when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH + "/pay", request);


        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("msg", String.class))
                    .contains(RESERVATION_IS_NOT_FOUND.name());
        });
    }

    @Test
    @DisplayName("결제를 진행할 유저가 존재하지 않으면 msg 에 RESERVATION_IS_NOT_FOUND 예외를 반환한다.")
    void payWithNoUser() {
        //given
        PaymentDto.Request request = PaymentDto.Request.builder()
                .reservationId(1L)
                .userId(1L)
                .token("jwt-token")
                .build();

        // when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH + "/pay", request);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("msg", String.class))
                    .contains(USER_IS_NOT_FOUND.name());
        });

    }

    @Test
    @DisplayName("결제 잔액이 부족하면 결제 시  msg 에 NOT_ENOUGH_BALANCE 예외를 반환한다.")
    void payWithNotEnoughMoney() {
        //given
        testDataHandler.settingUser(BigDecimal.valueOf(3000));

        PaymentDto.Request request = PaymentDto.Request.builder()
                .reservationId(1L)
                .userId(1L)
                .token("jwt-token")
                .build();

        // when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH + "/pay", request);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("msg", String.class))
                    .contains(USER_NOT_ENOUGH_BALANCE.name());
        });

    }

    @Test
    @DisplayName("한명의 유저가 동시에 1000개의 결제를 진행하면 한번만 결제가 성공하고 포인트가 차감되야 한다.")
    void payWhenConcurrency500EnvWithLock() throws InterruptedException {
        //given
        int numThreads = 500;
        int expectSuccessCnt = 1;
        int expectFailCnt = 499;

        testDataHandler.settingUser(BigDecimal.valueOf(1900000));

        CountDownLatch latch = new CountDownLatch(numThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        //when
        for (int i = 0; i < numThreads; i++) {
            executorService.execute(() -> {
                try {
                    PaymentCommand.Create command = new PaymentCommand.Create(1L, 1L, "jwt-token");
                    paymentFacade.pay(command);
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
            softly.assertThat(result).hasSize(2);
            softly.assertThat(result.get(0).getStatus()).isEqualTo(ReservationStatus.COMPLETED);
            softly.assertThat(successCount.get()).isEqualTo(expectSuccessCnt);
            softly.assertThat(failCount.get()).isEqualTo(expectFailCnt);
        });
    }

    @Test
    @DisplayName("한명의 유저가 동시에 다른 3개의 결제를 진행하면 모두 포인트가 차감되야 한다.")
    void payWhenConcurrency3EnvWithLock() throws InterruptedException {
        //given
        int numThreads = 3;
        int expectSuccessCnt = 3;
        int expectFailCnt = 0;

        testDataHandler.settingUser(BigDecimal.valueOf(510000)); //170000 * 3
        List<User> users = userRepository.getUsers();
        testDataHandler.reserveSeat(users.get(0).getUserId(), 35);
        List<ConcertReservationInfo> reservations = concertRepository.getReservations();
        Queue<Long> reservationIds = new ConcurrentLinkedDeque<>();

        for (ConcertReservationInfo reservation : reservations) {
            reservationIds.add(reservation.getReservationId());
        }

        CountDownLatch latch = new CountDownLatch(numThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        //when
        for (int i = 0; i < numThreads; i++) {
            executorService.execute(() -> {
                try {
                    PaymentCommand.Create command = new PaymentCommand.Create(reservationIds.poll(),
                            1L, "jwt-token");
                    paymentFacade.pay(command);
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

        List<Payment> result = paymentRepository.getPayments();
        List<User> resultUsers = userRepository.getUsers();

        // then
        assertSoftly(softly -> {
            softly.assertThat(result).hasSize(3);
            result.forEach(r -> softly.assertThat(r.getStatus()).isEqualTo(PaymentStatus.COMPLETE));
            softly.assertThat(resultUsers.get(0).getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
            softly.assertThat(successCount.get()).isEqualTo(expectSuccessCnt);
            softly.assertThat(failCount.get()).isEqualTo(expectFailCnt);
        });
    }

    @Test
    @DisplayName("포인트 충전과 결제가 동시에 이뤄지더라도 둘다 진행이 되어야 한다.")
    void payWithChargeAtTheSameTime() {
        //given
        testDataHandler.settingUser(BigDecimal.valueOf(170000));

        // when
        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> {
                    UserCommand.Create command = new UserCommand.Create(1L, BigDecimal.valueOf(170000));
                    userService.chargeBalance(command);
                }),
                CompletableFuture.runAsync(() -> userService.usePoint(1L, BigDecimal.valueOf(170000)))

        ).join();


        User result = userService.getUser(1L);

        // then
        assertSoftly(softly -> softly.assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(170000)));
    }

    @Test
    @DisplayName("결제를 진행하면 결제 이벤트를 잘 받아서 이벤트리스너가 실행된다.")
    void payWithEvent() {
        //given
        testDataHandler.settingUser(BigDecimal.valueOf(300000));

        PaymentDto.Request request = PaymentDto.Request.builder()
                .reservationId(1L)
                .userId(1L)
                .token("jwt-token")
                .build();

        // when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH + "/pay", request);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("data", PaymentDto.Response.class)
                    .status()).isEqualTo(PaymentStatus.COMPLETE);
        });

        verify(userEventListener, times(1)).onPaymentEvent(any(PaymentEvent.class));
        verify(queueEventListener, times(1)).onPaymentEvent(any(PaymentEvent.class));
    }

    @Test
    @DisplayName("aftercommit 전에 예외가 발생하면 모두 롤백되고,aftercommit 이벤트는 실행되지 않는다.")
    void payWithEventForRollbackAndNotInvokeAfterCommitEvent() {
        //given
        testDataHandler.settingUser(BigDecimal.valueOf(0));

        PaymentDto.Request request = PaymentDto.Request.builder()
                .reservationId(1L)
                .userId(1L)
                .token("jwt-token")
                .build();

        // when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH + "/pay", request);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("msg", String.class))
                    .contains(USER_NOT_ENOUGH_BALANCE.name());

        });

        verify(userEventListener, times(1)).onPaymentEvent(any(PaymentEvent.class));
        verify(queueEventListener, times(1)).onPaymentEvent(any(PaymentEvent.class));
    }

    @Test
    @DisplayName("결제를 진행하면 이벤트가 발행되고,kafka 메시지가 발행되면 outbox data가 상태가 done 이다.")
    void payWithEventAndSuccessToPublishKafkaMessage() {
        //given
        testDataHandler.settingUser(BigDecimal.valueOf(200000));

        PaymentDto.Request request = PaymentDto.Request.builder()
                .reservationId(1L)
                .userId(1L)
                .token("jwt-token")
                .build();

        post(LOCAL_HOST + port + PATH + "/pay", request);

        // when
        List<Outbox> result = outboxRepository.getOutboxes();

        // then
        verify(userEventListener, times(1)).onPaymentEvent(any(PaymentEvent.class));
        verify(queueEventListener, times(1)).onPaymentEvent(any(PaymentEvent.class));

        assertSoftly(softly -> {
            softly.assertThat(result.get(0).getOutboxId()).isEqualTo(1L);
            softly.assertThat(result.get(0).getStatus()).isEqualTo(Outbox.EventStatus.DONE);
        });
    }

    @Test
    @DisplayName("결제를 진행하면 이벤트가 발행되고,kafka 메시지 발행에 실패하면 outbox data가 상태가 init 이다.")
    void payWithEventAndFailToPublishKafkaMessage() {
        //given
        testDataHandler.settingUser(BigDecimal.valueOf(200000));

        PaymentDto.Request request = PaymentDto.Request.builder()
                .reservationId(1L)
                .userId(1L)
                .token("jwt-token")
                .build();

        doThrow(new CustomException(
                KAFKA_PUBLISH_FAILED, KAFKA_PUBLISH_FAILED.getMsg()))
                .when(eventProducer).publish(any(), any(String.class), any(String.class));

        post(LOCAL_HOST + port + PATH + "/pay", request);

        // when
        List<Outbox> result = outboxRepository.getOutboxes();


        // then
        verify(userEventListener, times(1)).onPaymentEvent(any(PaymentEvent.class));
        verify(queueEventListener, times(1)).onPaymentEvent(any(PaymentEvent.class));

        assertSoftly(softly -> {
            softly.assertThat(result.get(0).getOutboxId()).isEqualTo(1L);
            softly.assertThat(result.get(0).getStatus()).isEqualTo(Outbox.EventStatus.INIT);
        });
    }

    @Test
    @DisplayName("kafka 메시지 발행에 실패하면 outbox data가 상태가 init 이 되고, 스케줄러에 의해 재시도 로직을 수행한다.")
    void payWithEventAndFailToPublishKafkaMessageAndRetryPublishMessage() {
        //given
        testDataHandler.settingUser(BigDecimal.valueOf(200000));

        PaymentDto.Request request = PaymentDto.Request.builder()
                .reservationId(1L)
                .userId(1L)
                .token("jwt-token")
                .build();

        doThrow(new CustomException(
                KAFKA_PUBLISH_FAILED, KAFKA_PUBLISH_FAILED.getMsg()))
                .when(eventProducer).publish(any(), any(String.class), any(String.class));

        post(LOCAL_HOST + port + PATH + "/pay", request);

        // when
        outboxScheduler.retryFailEvent();

        List<Outbox> result = outboxRepository.getRetryOutboxes();

        // then
        verify(userEventListener, times(1)).onPaymentEvent(any(PaymentEvent.class));
        verify(queueEventListener, times(1)).onPaymentEvent(any(PaymentEvent.class));

        assertSoftly(softly -> {
            softly.assertThat(result.get(0).getOutboxId()).isEqualTo(1L);
            softly.assertThat(result.get(0).getStatus()).isEqualTo(Outbox.EventStatus.INIT);
            softly.assertThat(result.get(0).getRetryCount()).isEqualTo(1);
        });
    }

}
