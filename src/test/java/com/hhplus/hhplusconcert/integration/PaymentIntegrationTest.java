package com.hhplus.hhplusconcert.integration;

import com.hhplus.hhplusconcert.domain.concert.ConcertRepository;
import com.hhplus.hhplusconcert.domain.payment.Payment;
import com.hhplus.hhplusconcert.integration.common.BaseIntegrationTest;
import com.hhplus.hhplusconcert.integration.common.TestDataHandler;
import com.hhplus.hhplusconcert.interfaces.controller.payment.dto.PaymentDto;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;


class PaymentIntegrationTest extends BaseIntegrationTest {

    private static final String PATH = "/api/v1/payments";

    @Autowired
    TestDataHandler testDataHandler;


    @Autowired
    private ConcertRepository concertRepository;


    @Test
    @DisplayName("결제 요청을 하면 결제 완료 정보를 반환한다.")
    void pay() {
        //given
        testDataHandler.settingUser(BigDecimal.valueOf(300000));

        PaymentDto.Request request = PaymentDto.Request.builder()
                .reservationId(1L)
                .userId(1L)
                .build();

        // when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH + "/pay", request);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("data", PaymentDto.Response.class)
                    .status()).isEqualTo(Payment.PaymentStatus.COMPLETE);
        });
    }

    @Test
    @DisplayName("예약 완료할 예약건이 없으면 msg 에 RESERVATION_IS_NOT_FOUND 예외를 반환한다.")
    void payWithNotWaitStatus() {
        //given
        testDataHandler.settingUser(BigDecimal.valueOf(300000));
        concertRepository.deleteAll();

        PaymentDto.Request request = PaymentDto.Request.builder()
                .reservationId(1L)
                .userId(1L)
                .build();

        // when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH + "/pay", request);


        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("msg", String.class))
                    .contains(RESERVATION_IS_NOT_FOUND.name());
        });
    }

    @Test
    @DisplayName("결제를 진행할 유저가 존재하지 않으면 msg 에 RESERVATION_IS_NOT_FOUND 예외를 반환한다.")
    void payWithNoUser() {
        //given
        PaymentDto.Request request = PaymentDto.Request.builder()
                .reservationId(1L)
                .userId(1L)
                .build();

        // when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH + "/pay", request);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("msg", String.class))
                    .contains(USER_IS_NOT_FOUND.name());
        });

    }

    @Test
    @DisplayName("결제 잔액이 부족하면 결제 시  msg 에 NOT_ENOUGH_BALANCE 예외를 반환한다.")
    void payWithNotEnoughMoney() {
        //given
        testDataHandler.settingUser(BigDecimal.valueOf(3000));

        PaymentDto.Request request = PaymentDto.Request.builder()
                .reservationId(1L)
                .userId(1L)
                .build();

        // when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH + "/pay", request);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("msg", String.class))
                    .contains(USER_NOT_ENOUGH_BALANCE.name());
        });

    }

}
