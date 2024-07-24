package com.hhplus.hhplusconcert.application.user;

import com.hhplus.hhplusconcert.domain.user.User;
import com.hhplus.hhplusconcert.domain.user.UserService;
import com.hhplus.hhplusconcert.domain.user.command.UserCommand;
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
        UserCommand.Create command = new UserCommand.Create(1L, BigDecimal.valueOf(100000));
        User user = User.builder().balance(BigDecimal.valueOf(100000)).build();

        when(userService.getUser(command.userId())).thenReturn(user);

        // when
        User result = userFacade.getBalance(command.userId());

        // then
        assertThat(result.getBalance()).isEqualTo(BigDecimal.valueOf(100000));
    }

    @Test
    @DisplayName("유저 잔액을 충전하는 유즈케이스를 실행한다.")
    void chargeBalance() {
        // given
        Long userId = 1L;
        BigDecimal chargeAmount = BigDecimal.valueOf(100000);
        UserCommand.Create command = new UserCommand.Create(userId, chargeAmount);
        User user = User.builder().balance(chargeAmount).build();

        when(userService.chargeBalance(command)).thenReturn(user);

        // when
        User result = userFacade.chargeBalance(command);

        // then
        assertThat(result.getBalance()).isEqualTo(chargeAmount);

    }

}