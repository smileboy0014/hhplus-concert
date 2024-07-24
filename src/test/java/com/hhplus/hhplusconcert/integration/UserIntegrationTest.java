package com.hhplus.hhplusconcert.integration;

import com.hhplus.hhplusconcert.application.user.UserFacade;
import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.service.UserAppender;
import com.hhplus.hhplusconcert.domain.user.service.UserFinder;
import com.hhplus.hhplusconcert.domain.user.service.dto.UserInfo;
import com.hhplus.hhplusconcert.domain.user.service.dto.UserServiceRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.CHARGE_AMOUNT_IS_NEGATIVE;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.USER_IS_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class UserIntegrationTest {

    @Autowired
    private UserFinder userFinder;
    @Autowired
    private UserAppender userAppender;
    @Autowired
    private UserFacade userFacade;

    @BeforeEach
    void setUp() {

        userAppender.appendUser(User.builder()
                .balance(BigDecimal.valueOf(50000))
                .build());
    }

    @AfterEach
    void tearDown() {
        userAppender.deleteAll();
    }


    @Test
    @DisplayName("유저 잔액을 조회하면 잔액 정보를 반환한다.")
    void getBalance() {
        // given
        List<User> users = userFinder.findUsers();

        // when
        UserInfo result = userFacade.getBalance(users.get(0).getUserId());

        // then
        assertThat(result.balance()).isEqualByComparingTo(BigDecimal.valueOf(50000));
    }

    @Test
    @DisplayName("잔액 조회 할 유저가 존재하지 않다면 USER_IS_NOT_FOUND 예외를 반환한다.")
    void getBalanceWithNoUser() {
        // given
        Long userId = 100000L;

        // when // then
        assertThatThrownBy(() -> userFacade.getBalance(userId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_IS_NOT_FOUND);
    }

    @Test
    @DisplayName("유저 잔액을 충전하면 현재 잔액 정보를 반환한다.")
    void chargeBalance() {
        // given
        BigDecimal chargeAmount = BigDecimal.valueOf(100000);
        BigDecimal remainAmount = BigDecimal.valueOf(150000);

        List<User> users = userFinder.findUsers();

        UserServiceRequest request = UserServiceRequest
                .builder()
                .userId(users.get(0).getUserId())
                .balance(chargeAmount)
                .build();

        // when
        UserInfo result = userFacade.chargeBalance(request);

        // then
        assertThat(result.balance()).isEqualByComparingTo(remainAmount);
    }

    @Test
    @DisplayName("유저 잔액 충전 요청이 동시에 따다닥 들어와도 한번만 충전된다.")
    void chargeBalanceAtTheSameTime() throws InterruptedException {

        // given
        int numThreads = 3;
        int expectSuccessCnt = 1;
        int expectFailCnt = 2;

        List<User> users = userFinder.findUsers();
        BigDecimal chargeAmount = BigDecimal.valueOf(100000);
        BigDecimal initialAmount = users.get(0).getBalance();
        BigDecimal expectedAmount = initialAmount.add(chargeAmount);

        UserServiceRequest request = UserServiceRequest
                .builder()
                .userId(users.get(0).getUserId())
                .balance(chargeAmount)
                .build();

        CountDownLatch latch = new CountDownLatch(numThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        //when
        for (int i = 0; i < numThreads; i++) {
            executorService.execute(() -> {
                try {
                    userFacade.chargeBalance(request);
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

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBalance()).isEqualTo(expectedAmount);
        assertThat(successCount.get()).isEqualTo(expectSuccessCnt);
        assertThat(failCount.get()).isEqualTo(expectFailCnt);
    }


    @Test
    @DisplayName("0보다 작은 잔액을 충전하려고 하면 CHARGE_AMOUNT_IS_NEGATIVE 예외를 반환한다.")
    void chargeBalanceWithNegativePoint() {
        // given
        List<User> users = userFinder.findUsers();
        BigDecimal chargeAmount = BigDecimal.valueOf(-1000);

        UserServiceRequest request = UserServiceRequest
                .builder()
                .userId(users.get(0).getUserId())
                .balance(chargeAmount)
                .build();

        // when // then
        assertThatThrownBy(() -> userFacade.chargeBalance(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(CHARGE_AMOUNT_IS_NEGATIVE);
    }


}

