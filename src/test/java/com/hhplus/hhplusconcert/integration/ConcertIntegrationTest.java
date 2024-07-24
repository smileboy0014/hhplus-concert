package com.hhplus.hhplusconcert.integration;

import com.hhplus.hhplusconcert.domain.concert.ConcertRepository;
import com.hhplus.hhplusconcert.integration.common.BaseIntegrationTest;
import com.hhplus.hhplusconcert.interfaces.controller.concert.dto.ConcertDateDto;
import com.hhplus.hhplusconcert.interfaces.controller.concert.dto.ConcertDto;
import com.hhplus.hhplusconcert.interfaces.controller.concert.dto.ConcertSeatDto;
import com.hhplus.hhplusconcert.support.utils.DateUtils;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class ConcertIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ConcertRepository concertRepository;

    private static final String PATH = "/api/v1/concerts";

    @Test
    @DisplayName("콘서트 목록을 조회한다.")
    void getConcerts() {
        //given //when
        ExtractableResponse<Response> result = get(LOCAL_HOST + port + PATH);

        //then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getList("data").size()).isEqualTo(2);
        });
    }

    @Test
    @DisplayName("콘서트 정보가 없으면 빈 배열을 반환한다.")
    void getConcertsWithEmptyList() {
        //given
        concertRepository.deleteAll();

        //when
        ExtractableResponse<Response> result = get(LOCAL_HOST + port + PATH);

        //then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getList("data")).isEqualTo(new ArrayList<>());
        });
    }

    @Test
    @DisplayName("콘서트 상세 정보를 조회한다.")
    void getConcert() {
        //given
        long concertId = 1L;

        //when
        ExtractableResponse<Response> result = get(LOCAL_HOST + port + PATH + "/" + concertId);

        //then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getObject("data", ConcertDto.Response.class).name()).isEqualTo("싸이 흠뻑쇼");
        });
    }

    @Test
    @DisplayName("등록되지 않는 콘서트 정보를 조회하면 msg 에 CONCERT_IS_NOT_FOUND 를 반환한다.")
    void getConcertWithNoConcert() {
        //given
        long concertId = 1000L;

        //when
        ExtractableResponse<Response> result = get(LOCAL_HOST + port + PATH + "/" + concertId);

        //then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getBoolean("success")).isFalse();
            softly.assertThat(result.body().jsonPath().getObject("msg", String.class))
                    .contains(CONCERT_IS_NOT_FOUND.name());
        });
    }

    @Test
    @DisplayName("예약 가능한 콘서트 날짜를 조회한다.")
    void getConcertDates() {
        //given
        long concertId = 1L;

        //when
        ExtractableResponse<Response> result = get(LOCAL_HOST + port + PATH + "/" + concertId + "/dates");

        //then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getList("data", ConcertDateDto.Response.class).get(0).concertDate())
                    .isEqualTo(DateUtils.getLocalDateTimeToString(LocalDateTime.now().plusMonths(3)));
        });
    }

    @Test
    @DisplayName("예정된 콘서트 날짜가 없으면, msg 에 CONCERT_DATE_IS_NOT_FOUND 를 반환한다.")
    void getConcertDatesWithNoDates() {
        //given
        long concertId = 10000L;

        //when
        ExtractableResponse<Response> result = get(LOCAL_HOST + port + PATH + "/" + concertId + "/dates");

        //then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getBoolean("success")).isFalse();
            softly.assertThat(result.body().jsonPath().getObject("msg", String.class))
                    .contains(CONCERT_DATE_IS_NOT_FOUND.name());
        });
    }

    @Test
    @DisplayName("예약 가능한 좌석을 조회한다.")
    void getAvailableSeats() {
        //given
        long concertDateId = 1L;

        //when
        ExtractableResponse<Response> result = get(LOCAL_HOST + port + PATH + "/dates/" + concertDateId + "/seats");

        //then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getList("data", ConcertSeatDto.Response.class).size()).isEqualTo(18);
        });
    }

    @Test
    @DisplayName("예약할 수 있는 좌석이 없다면, msg 에 SEAT_IS_NOT_FOUND 를 반환한다.")
    void getAvailableSeatsWithNoSeats() {
        //given
        long concertDateId = 100000L;

        //when
        ExtractableResponse<Response> result = get(LOCAL_HOST + port + PATH + "/dates/" + concertDateId + "/seats");

        //then
        assertSoftly(softly -> {
            softly.assertThat(result.statusCode()).isEqualTo(200);
            softly.assertThat(result.body().jsonPath().getBoolean("success")).isFalse();
            softly.assertThat(result.body().jsonPath().getObject("msg", String.class))
                    .contains(SEAT_IS_NOT_FOUND.name());
        });
    }

}
