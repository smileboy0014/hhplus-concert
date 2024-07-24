package com.hhplus.hhplusconcert.integration;

import com.hhplus.hhplusconcert.application.queue.WaitingQueueFacade;
import com.hhplus.hhplusconcert.domain.queue.WaitingQueue;
import com.hhplus.hhplusconcert.domain.user.User;
import com.hhplus.hhplusconcert.integration.common.BaseIntegrationTest;
import com.hhplus.hhplusconcert.integration.common.TestDataHandler;
import com.hhplus.hhplusconcert.interfaces.controller.queue.dto.TokenDto;
import com.hhplus.hhplusconcert.interfaces.controller.queue.dto.WaitingQueueDto;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.ALREADY_TOKEN_IS_ACTIVE;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.USER_IS_NOT_FOUND;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class WaitingQueueIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WaitingQueueFacade waitingQueueFacade;

    @Autowired
    private TestDataHandler testDataHandler;

    private static final String PATH = "/api/v1/queues";

    @Test
    @DisplayName("유저가 토큰 발급을 요청할 떄 토큰 활성화 정보를 반환한다.")
    void issueToken() {
        //given
        testDataHandler.settingUser(BigDecimal.ZERO);

        TokenDto.Request request = TokenDto.Request.builder()
                .userId(1L)
                .build();

        //when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH + "/issue-token", request);

        //then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("data", WaitingQueueDto.Response.class).isActive()).isEqualTo(true);
        });
    }

    @Test
    @DisplayName("유저가 토큰 발급을 요청할 떄 활성화 유저가 꽉 차면 토큰 정보와 대기열 정보를 반환한다.")
    void issueTokenWithWaitingInfo() {
        // given
        for (long i = 0; i < 50; i++) {
            User user = testDataHandler.settingUser(BigDecimal.ZERO);

            TokenDto.Request request = TokenDto.Request.builder()
                    .userId(user.getUserId())
                    .build();

            waitingQueueFacade.issueToken(request.toCreateCommand());
        }

        User user = testDataHandler.settingUser(BigDecimal.ZERO);

        TokenDto.Request request = TokenDto.Request.builder()
                .userId(user.getUserId())
                .build();

        // when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH + "/issue-token", request);

        //then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("data", WaitingQueueDto.Response.class).isActive()).isEqualTo(false);
        });
    }

    @Test
    @DisplayName("등록되지 않은 유저가 토큰 발급을 요청하면 msg 에 USER_IS_NOT_FOUND 반환한다.")
    void issueTokenWithNoUser() {
        // given
        TokenDto.Request request = TokenDto.Request.builder()
                .userId(1L)
                .build();

        //when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH + "/issue-token", request);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("msg", String.class))
                    .contains(USER_IS_NOT_FOUND.name());
        });
    }

    @Test
    @DisplayName("유저의 대기열 정보를 반환한다.")
    void checkQueue() {
        // given
        for (long i = 0; i < 55; i++) {
            User user = testDataHandler.settingUser(BigDecimal.ZERO);

            TokenDto.Request tokenRequest = TokenDto.Request.builder()
                    .userId(user.getUserId())
                    .build();

            waitingQueueFacade.issueToken(tokenRequest.toCreateCommand());
        }

        User user = testDataHandler.settingUser(BigDecimal.ZERO);

        TokenDto.Request tokenRequest = TokenDto.Request.builder()
                .userId(user.getUserId())
                .build();

        WaitingQueue tokenInfo = waitingQueueFacade.issueToken(tokenRequest.toCreateCommand());

        WaitingQueueDto.Request request = WaitingQueueDto.Request.builder()
                .userId(user.getUserId())
                .token(tokenInfo.getToken())
                .build();

        // when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH + "/check", request);

        //then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("data", WaitingQueueDto.Response.class)
                    .waitingInfo().waitingNumber()).isEqualTo(5L);
        });
    }

    @Test
    @DisplayName("이미 유저의 토큰이 활성화 되었다면 msg 에 ALREADY_TOKEN_IS_ACTIVE 를 반환한다.")
    void checkQueueWithAlreadyActiveToken() {
        // given
        for (long i = 0; i < 49; i++) {
            User user = testDataHandler.settingUser(BigDecimal.ZERO);

            TokenDto.Request tokenRequest = TokenDto.Request.builder()
                    .userId(user.getUserId())
                    .build();

            waitingQueueFacade.issueToken(tokenRequest.toCreateCommand());
        }

        User user = testDataHandler.settingUser(BigDecimal.ZERO);

        TokenDto.Request tokenRequest = TokenDto.Request.builder()
                .userId(user.getUserId())
                .build();

        WaitingQueue tokenInfo = waitingQueueFacade.issueToken(tokenRequest.toCreateCommand());

        WaitingQueueDto.Request request = WaitingQueueDto.Request.builder()
                .userId(user.getUserId())
                .token(tokenInfo.getToken())
                .build();

        // when
        ExtractableResponse<Response> result = post(LOCAL_HOST + port + PATH + "/check", request);

        //then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("msg", String.class))
                    .contains(ALREADY_TOKEN_IS_ACTIVE.name());
        });
    }
}
