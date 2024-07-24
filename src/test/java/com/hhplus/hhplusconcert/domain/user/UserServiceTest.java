package com.hhplus.hhplusconcert.domain.user;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    UserRepository userRepository;

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

        when(userRepository.getUser(user.getUserId())).thenReturn(Optional.of(user));

        // when
        User result = userService.getUser(user.getUserId());

        // then
        assertThat(result.getBalance()).isEqualTo(BigDecimal.valueOf(50000));
    }

    @Test
    @DisplayName("잔액 조회 할 유저가 존재하지 않다면 USER_IS_NOT_FOUND 예외를 반환한다.")
    void getBalanceWithNoUser() {
        // given
        Long userId = 1L;

        // when // then
        assertThatThrownBy(() -> userService.getUser(userId))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_IS_NOT_FOUND);
    }

    @Test
    @DisplayName("환불된 정보가 유저 잔액에 더해진다.")
    void refund() {
        // given
        User user = User
                .builder()
                .userId(1L)
                .balance(BigDecimal.ZERO)
                .build();
        BigDecimal refundPoint = BigDecimal.valueOf(50000);

        when(userRepository.getUser(user.getUserId())).thenReturn(Optional.of(user));

        // when
        userService.refund(user.getUserId(), refundPoint);

        // then
        verify(userRepository).saveUser(user);
        assertThat(user.getBalance()).isEqualTo(refundPoint);
    }

    @Test
    @DisplayName("환불 대상인 유저가 존재하지 않는다면 USER_IS_NOT_FOUND 예외를 반환한다.")
    void refundWithNoUser() {
        // given
        Long userId = 1L;

        // when // then
        assertThatThrownBy(() -> userService.refund(userId, BigDecimal.valueOf(100000)))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_IS_NOT_FOUND);
    }

    @Test
    @DisplayName("0 이하의 포인트를 환불 받으려고 하면 USER_CHARGE_AMOUNT_IS_NEGATIVE 예외를 반환한다.")
    void refundWithNegativePoint() {
        // given
        User user = User
                .builder()
                .userId(1L)
                .balance(BigDecimal.ZERO)
                .build();
        BigDecimal refundPoint = BigDecimal.valueOf(-50000);

        when(userRepository.getUser(user.getUserId())).thenReturn(Optional.of(user));

        // when // then
        assertThatThrownBy(() -> userService.refund(user.getUserId(), refundPoint))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_CHARGE_AMOUNT_IS_NEGATIVE);
    }

    @Test
    @DisplayName("유저의 포인트를 사용한다.")
    void usePoint() {
        // given
        User user = User
                .builder()
                .userId(1L)
                .balance(BigDecimal.valueOf(100000))
                .build();
        BigDecimal usePoint = BigDecimal.valueOf(50000);

        when(userRepository.getUser(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.saveUser(any(User.class))).thenReturn(Optional.of(user));

        // when
        User result = userService.usePoint(user.getUserId(), usePoint);

        // then
        assertThat(result.getBalance()).isEqualTo(BigDecimal.valueOf(50000));
    }

    @Test
    @DisplayName("포인트를 사용할 유저가 존재하지 않으면 USER_IS_NOT_FOUND 예외를 반환한다.")
    void usePointWithNoUser() {
        // given
        Long userId = 1L;
        BigDecimal usePoint = BigDecimal.valueOf(10000);

        // when // then
        assertThatThrownBy(() -> userService.usePoint(userId, usePoint))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_IS_NOT_FOUND);
    }

    @Test
    @DisplayName("유저의 잔액이 부족하다면 USER_NOT_ENOUGH_BALANCE 예외를 반환한다.")
    void usePointWithNotEnoughBalance() {
        // given
        User user = User
                .builder()
                .userId(1L)
                .balance(BigDecimal.valueOf(30000))
                .build();
        BigDecimal usePoint = BigDecimal.valueOf(50000);

        when(userRepository.getUser(user.getUserId())).thenReturn(Optional.of(user));

        // when // then
        assertThatThrownBy(() -> userService.usePoint(user.getUserId(), usePoint))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_ENOUGH_BALANCE);
    }

}