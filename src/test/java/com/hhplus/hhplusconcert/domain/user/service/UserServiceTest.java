package com.hhplus.hhplusconcert.domain.user.service;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.service.dto.UserInfo;
import com.hhplus.hhplusconcert.domain.user.service.dto.UserServiceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.USER_IS_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserFinder userFinder;
    @Mock
    private UserReader userReader;

    @InjectMocks
    UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("유저 잔액을 조회하면 잔액 정보를 반환한다.")
    void getBalance() {
        // given
        User user = User
                .builder()
                .userId(1L)
                .balance(BigDecimal.valueOf(50000))
                .build();

        UserInfo userInfo = UserInfo.of(user);

        when(userFinder.findUserByUserId(user.getUserId())).thenReturn(user);
        when(userReader.readUser(user)).thenReturn(userInfo);

        // when
        UserInfo result = userService.getBalance(user.getUserId());

        // then
        assertThat(result.balance()).isEqualTo(BigDecimal.valueOf(50000));
    }

    @Test
    @DisplayName("잔액 조회 할 유저가 존재하지 않다면 USER_IS_NOT_FOUND 예외를 반환한다.")
    void getBalanceWithNoUser() {
        // given
        Long userId = 1L;

        when(userFinder.findUserByUserId(userId)).thenThrow(
                new CustomException(USER_IS_NOT_FOUND,
                        USER_IS_NOT_FOUND.getMsg()));

        // when // then
        assertThatThrownBy(() -> userService.getBalance(userId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_IS_NOT_FOUND);
    }

    @Test
    @DisplayName("유저 잔액을 충전하면 현재 잔액 정보를 반환한다.")
    void chargeBalance() {
        // given
        Long userId = 1L;
        BigDecimal chargeAmount = BigDecimal.valueOf(50000);

        UserServiceRequest request = UserServiceRequest
                .builder()
                .userId(userId)
                .balance(chargeAmount)
                .build();

        User user = User
                .builder()
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .build();

        User resultUser = User
                .builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(50000))
                .build();

        UserInfo userInfo = UserInfo.of(resultUser);

        when(userFinder.findUserByUserIdWithLock(userId)).thenReturn(user);
        when(userReader.readUser(user)).thenReturn(userInfo);

        // when
        UserInfo result = userService.chargeBalance(request);

        // then
        assertThat(result.balance()).isEqualTo(chargeAmount);
    }

    @Test
    @DisplayName("잔액을 충전할 유저가 없으면 USER_IS_NOT_FOUND 예외를 반환한다.")
    void chargeBalanceWithNoUser() {
        // given
        Long userId = 1L;
        BigDecimal chargeAmount = BigDecimal.valueOf(50000);

        UserServiceRequest request = UserServiceRequest
                .builder()
                .userId(userId)
                .balance(chargeAmount)
                .build();

        when(userFinder.findUserByUserIdWithLock(userId)).thenThrow(
                new CustomException(USER_IS_NOT_FOUND, USER_IS_NOT_FOUND.getMsg()));

        // when // then
        assertThatThrownBy(() -> userService.chargeBalance(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_IS_NOT_FOUND);
    }
}