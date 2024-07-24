package com.hhplus.hhplusconcert.domain.user;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.USER_CHARGE_AMOUNT_IS_NEGATIVE;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.USER_NOT_ENOUGH_BALANCE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    @DisplayName("잔액을 충전한다.")
    void chargeBalance() {
        // given
        BigDecimal amount = BigDecimal.valueOf(10000);
        User user = User.builder().balance(BigDecimal.ZERO).build();

        // when
        user.chargeBalance(amount);

        // then
        Assertions.assertThat(user.getBalance()).isEqualTo(amount);
    }

    @Test
    @DisplayName("0 이하의 포인트를 충전하는 경우 USER_CHARGE_AMOUNT_IS_NEGATIVE 예외를 반환한다.")
    void chargeBalanceWithNegativeChargePoint() {
        // given
        BigDecimal amount = BigDecimal.valueOf(-10000);
        User user = User.builder().balance(BigDecimal.ZERO).build();

        // when // then
        assertThatThrownBy(() -> user.chargeBalance(amount))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_CHARGE_AMOUNT_IS_NEGATIVE);
    }

    @Test
    @DisplayName("포인트를 사용한다.")
    void useBalance() {
        // given
        BigDecimal amount = BigDecimal.valueOf(10000);
        User user = User.builder().balance(amount).build();

        // when
        user.useBalance(amount);

        // then
        Assertions.assertThat(user.getBalance()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("가지고 있는 포인트 이상의 포인트를 사용하려고 하면 USER_NOT_ENOUGH_BALANCE 예외를 반환한다.")
    void useBalanceWithOverUsePoint() {
        // given
        BigDecimal amount = BigDecimal.valueOf(10000);
        BigDecimal useAmount = BigDecimal.valueOf(20000);

        User user = User.builder().balance(amount).build();

        // when
        assertThatThrownBy(() -> user.useBalance(useAmount))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_NOT_ENOUGH_BALANCE);
    }

}