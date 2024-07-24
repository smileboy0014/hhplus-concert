package com.hhplus.hhplusconcert.integration;

import com.hhplus.hhplusconcert.integration.common.BaseIntegrationTest;
import com.hhplus.hhplusconcert.integration.common.TestDataHandler;
import com.hhplus.hhplusconcert.interfaces.controller.user.dto.UserBalanceDto;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.USER_CHARGE_AMOUNT_IS_NEGATIVE;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.USER_IS_NOT_FOUND;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class UserIntegrationTest extends BaseIntegrationTest {


    @Autowired
    private TestDataHandler testDataHandler;

    private static final String PATH = "/api/v1/users";


    @Test
    @DisplayName("유저 잔액을 조회하면 잔액 정보를 반환한다.")
    void getBalance() {
        // given
        long userId = 1L;
        BigDecimal amount = BigDecimal.valueOf(300000);
        testDataHandler.settingUser(amount);

        // when
        ExtractableResponse<Response> result = get(LOCAL_HOST + port + PATH + "/" + userId + "/balance");

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("data", UserBalanceDto.Response.class)
                    .balance()).isEqualByComparingTo(amount);
        });
    }

    @Test
    @DisplayName("잔액 조회 할 유저가 존재하지 않다면 msg 에 USER_IS_NOT_FOUND 예외를 반환한다.")
    void getBalanceWithNoUser() {
        // given
        long userId = 1000L;

        // when
        ExtractableResponse<Response> result = get(LOCAL_HOST + port + PATH + "/" + userId + "/balance");

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("msg", String.class))
                    .contains(USER_IS_NOT_FOUND.name());
        });
    }

    @Test
    @DisplayName("유저 잔액을 충전하면 현재 잔액 정보를 반환한다.")
    void chargeBalance() {
        // given
        long userId = 1L;
        testDataHandler.settingUser(BigDecimal.valueOf(50000));
        BigDecimal chargeAmount = BigDecimal.valueOf(50000);

        UserBalanceDto.Request request = UserBalanceDto.Request.builder()
                .balance(chargeAmount)
                .build();

        // when
        ExtractableResponse<Response> result = patch(LOCAL_HOST + port + PATH + "/" + userId + "/charge", request);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("data", UserBalanceDto.Response.class)
                    .balance()).isEqualByComparingTo(BigDecimal.valueOf(100000));
        });
    }


    @Test
    @DisplayName("0보다 작은 잔액을 충전하려고 하면 msg 에 USER_CHARGE_AMOUNT_IS_NEGATIVE 예외를 반환한다.")
    void chargeBalanceWithNegativePoint() {
        // given
        long userId = 1L;
        testDataHandler.settingUser(BigDecimal.ZERO);
        BigDecimal chargeAmount = BigDecimal.valueOf(-5000);


        UserBalanceDto.Request request = UserBalanceDto.Request.builder()
                .balance(chargeAmount)
                .build();

        // when
        ExtractableResponse<Response> result = patch(LOCAL_HOST + port + PATH + "/" + userId + "/charge", request);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("msg", String.class))
                    .contains(USER_CHARGE_AMOUNT_IS_NEGATIVE.name());
        });
    }
}

