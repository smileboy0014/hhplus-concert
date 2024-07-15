package com.hhplus.hhplusconcert.integration;

import com.hhplus.hhplusconcert.application.user.UserFacade;
import com.hhplus.hhplusconcert.domain.common.exception.CustomNotFoundException;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.repository.UserRepository;
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

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.USER_IS_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class UserIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFacade userFacade;

    @BeforeEach
    void setUp() {

        userRepository.addUser(User.builder()
                .balance(BigDecimal.valueOf(50000))
                .build());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }


    @Test
    @DisplayName("유저 잔액을 조회하면 잔액 정보를 반환한다.")
    void getBalance() {
        // given
        List<User> users = userRepository.findAll();

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
                .isInstanceOf(CustomNotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(USER_IS_NOT_FOUND);
    }

    @Test
    @DisplayName("유저 잔액을 충전하면 현재 잔액 정보를 반환한다.")
    void chargeBalance() {
        // given
        BigDecimal chargeAmount = BigDecimal.valueOf(100000);
        BigDecimal remainAmount = BigDecimal.valueOf(150000);

        List<User> users = userRepository.findAll();

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
    @DisplayName("잔액 조회 할 유저가 존재하지 않다면 USER_IS_NOT_FOUND 예외를 반환한다.")
    void chargeBalanceWithNoUser() {
        // given
        Long userId = 100000L;
        BigDecimal chargeAmount = BigDecimal.valueOf(100000);

        UserServiceRequest request = UserServiceRequest
                .builder()
                .userId(userId)
                .balance(chargeAmount)
                .build();

        // when // then
        assertThatThrownBy(() -> userFacade.chargeBalance(request))
                .isInstanceOf(CustomNotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(USER_IS_NOT_FOUND);
    }

}

