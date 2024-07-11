package com.hhplus.hhplusconcert.domain.user.service;

import com.hhplus.hhplusconcert.domain.common.exception.CustomNotFoundException;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.repository.UserRepository;
import com.hhplus.hhplusconcert.domain.user.service.dto.UserResponse;
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

        when(userRepository.findUserByUserId(user.getUserId())).thenReturn(user);

        // when
        UserResponse result = userService.getBalance(user.getUserId());

        // then
        assertThat(result.balance()).isEqualTo(BigDecimal.valueOf(50000));
    }

    @Test
    @DisplayName("잔액 조회 할 유저가 존재하지 않다면 USER_IS_NOT_FOUND 예외를 반환한다.")
    void getBalanceWithNoUser() {
        // given
        Long userId = 1L;

        when(userRepository.findUserByUserId(userId)).thenThrow(
                new CustomNotFoundException(USER_IS_NOT_FOUND,
                        USER_IS_NOT_FOUND.getMsg()));

        // when // then
        assertThatThrownBy(() -> userService.getBalance(userId))
                .isInstanceOf(CustomNotFoundException.class)
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

        when(userRepository.findUserByUserId(userId)).thenReturn(user);

        // when
        UserResponse result = userService.chargeBalance(request);

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

        when(userRepository.findUserByUserId(userId)).thenThrow(
                new CustomNotFoundException(USER_IS_NOT_FOUND, USER_IS_NOT_FOUND.getMsg()));

        // when // then
        assertThatThrownBy(() -> userService.chargeBalance(request))
                .isInstanceOf(CustomNotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(USER_IS_NOT_FOUND);
    }


}