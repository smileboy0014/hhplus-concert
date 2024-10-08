package com.hhplus.hhplusconcert.integration;

import com.hhplus.hhplusconcert.application.queue.WaitingQueueFacade;
import com.hhplus.hhplusconcert.domain.queue.WaitingQueueRepository;
import com.hhplus.hhplusconcert.domain.queue.command.WaitingQueueCommand;
import com.hhplus.hhplusconcert.domain.user.User;
import com.hhplus.hhplusconcert.domain.user.UserRepository;
import com.hhplus.hhplusconcert.integration.common.BaseIntegrationTest;
import com.hhplus.hhplusconcert.integration.common.TestDataHandler;
import com.hhplus.hhplusconcert.interfaces.controller.queue.dto.WaitingQueueDto;
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

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.USER_IS_NOT_FOUND;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class WaitingQueueIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WaitingQueueFacade waitingQueueFacade;

    @Autowired
    private WaitingQueueRepository waitingQueueRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestDataHandler testDataHandler;

    private static final String PATH = "/api/v1/queues";

    @Test
    @DisplayName("유저가 토큰 발급을 요청할 떄 토큰 활성화 정보를 반환한다.")
    void issueToken() {
        //given
        testDataHandler.settingUser(BigDecimal.ZERO);

        WaitingQueueDto.Request request = WaitingQueueDto.Request.builder()
                .userId(1L)
                .build();

        //when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH + "/token", request);

        //then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("data", WaitingQueueDto.Response.class).isActive()).isEqualTo(true);
        });
    }

    @Test
    @DisplayName("유저가 토큰 발급을 요청할 떄 활성화 유저가 꽉 차면 토큰 정보와 대기열 정보를 반환한다.")
    void issueTokenWithWaitingInfo() {
        // given
        for (long i = 0; i < 1001; i++) {
            User user = testDataHandler.settingUser(BigDecimal.ZERO);

            WaitingQueueDto.Request request = WaitingQueueDto.Request.builder()
                    .userId(user.getUserId())
                    .build();

            waitingQueueFacade.checkWaiting(request.toCreateCommand());
        }

        User user = testDataHandler.settingUser(BigDecimal.ZERO);

        WaitingQueueDto.Request request = WaitingQueueDto.Request.builder()
                .userId(user.getUserId())
                .build();

        // when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH + "/token", request);

        //then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("data", WaitingQueueDto.Response.class).isActive()).isEqualTo(false);
        });
    }

    @Test
    @DisplayName("등록되지 않은 유저가 토큰 발급을 요청하면 msg 에 USER_IS_NOT_FOUND 반환한다.")
    void issueTokenWithNoUser() {
        // given
        WaitingQueueDto.Request request = WaitingQueueDto.Request.builder()
                .userId(1L)
                .build();

        //when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH + "/token", request);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("msg", String.class))
                    .contains(USER_IS_NOT_FOUND.name());
        });
    }

    @Test
    @DisplayName("유저의 대기열 정보를 반환한다.")
    void checkQueue() {
        // given
        for (long i = 0; i < 1001; i++) {
            User user = testDataHandler.settingUser(BigDecimal.ZERO);

            WaitingQueueDto.Request request = WaitingQueueDto.Request.builder()
                    .userId(user.getUserId())
                    .build();

            waitingQueueFacade.checkWaiting(request.toCreateCommand());
        }

        User user = testDataHandler.settingUser(BigDecimal.ZERO);

        WaitingQueueDto.Request request = WaitingQueueDto.Request.builder()
                .userId(user.getUserId())
                .build();

        // when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH + "/token", request);

        //then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("data", WaitingQueueDto.Response.class)
                    .waitingInfo().waitingNumber()).isEqualTo(1L);
        });
    }

    @Test
    @DisplayName("동시에 1002명의 유저들이 토큰을 발급받으면 1000명만 active 토큰으로 활성화되고, 나머지는 다 waiting 상태의 토큰을 받는다.")
    void checkQueueWhenConcurrency1000EnvWithLock() throws InterruptedException {
        //given
        int numThreads = 1002;

        for (long i = 0; i < 1002; i++) testDataHandler.settingUser(BigDecimal.ZERO);

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
                    WaitingQueueCommand.Create command = new WaitingQueueCommand.Create(userIds.poll(), null);
                    waitingQueueFacade.checkWaiting(command);
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


        long result = waitingQueueRepository.getActiveCnt();

        // then
        assertSoftly(softly -> softly.assertThat(result).isEqualTo(1000));
    }
}
