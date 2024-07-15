package com.hhplus.hhplusconcert.application.user;

import com.hhplus.hhplusconcert.domain.user.service.UserService;
import com.hhplus.hhplusconcert.domain.user.service.dto.UserInfo;
import com.hhplus.hhplusconcert.domain.user.service.dto.UserServiceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class UserFacadeTest {

    @Mock
    UserService userService;

    @InjectMocks
    UserFacade userFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("유저 잔액을 조회하는 유즈케이스를 실행한다.")
    void getBalance() {
        // given
        UserInfo response = UserInfo
                .builder()
                .userId(1L)
                .balance(BigDecimal.valueOf(100000))
                .build();

        when(userService.getBalance(response.userId())).thenReturn(response);

        // when
        UserInfo result = userFacade.getBalance(response.userId());

        // then
        assertThat(result.balance()).isEqualTo(BigDecimal.valueOf(100000));
    }

    @Test
    @DisplayName("유저 잔액을 충전하는 유즈케이스를 실행한다.")
    void chargeBalance() {
        Long userId = 1L;
        BigDecimal chargeAmount = BigDecimal.valueOf(100000);

        // given
        UserServiceRequest request = UserServiceRequest
                .builder()
                .userId(userId)
                .balance(chargeAmount)
                .build();

        UserInfo response = UserInfo.builder()
                .userId(userId)
                .balance(chargeAmount)
                .build();

        when(userService.chargeBalance(request)).thenReturn(response);

        // when
        UserInfo result = userFacade.chargeBalance(request);

        // then
        assertThat(result.balance()).isEqualTo(chargeAmount);

    }

}