package com.hhplus.hhplusconcert.interfaces.controller;

import com.hhplus.hhplusconcert.application.concert.ConcertFacade;
import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.concert.Concert;
import com.hhplus.hhplusconcert.domain.concert.ConcertDate;
import com.hhplus.hhplusconcert.domain.concert.Place;
import com.hhplus.hhplusconcert.domain.concert.Seat;
import com.hhplus.hhplusconcert.interfaces.controller.concert.ConcertController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.CONCERT_DATE_IS_NOT_FOUND;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.SEAT_IS_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConcertController.class)
class ConcertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConcertFacade concertFacade;

    @Test
    @DisplayName("콘서트 목록을 조회한다.")
    void getConcerts() throws Exception {
        //given
        Page<Concert> response = new PageImpl<>(List.of(Concert.builder()
                        .concertId(1L)
                        .name("싸이 흠뻑쇼")
                        .build(),
                Concert.builder()
                        .concertId(2L)
                        .name("싸이 흠뻑쇼")
                        .build()));

        when(concertFacade.getConcerts(any(Pageable.class))).thenReturn(response);

        //when //then
        mockMvc.perform(get("/v1/concerts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("OK"))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @DisplayName("콘서트 목록이 없으면 빈 배열을 반환한다.")
    void getConcertsWithEmpty() throws Exception {
        //given
//        List<Concert> response = List.of();
        when(concertFacade.getConcerts(any(Pageable.class))).thenReturn(Page.empty());

        //when //then
        mockMvc.perform(get("/v1/concerts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("OK"))
                .andExpect(jsonPath("$.data.content").isEmpty());
    }

    @Test
    @DisplayName("콘서트 상세 정보를 조회한다.")
    void getConcert() throws Exception {
        //given
        Long concertId = 1L;
        Concert response = Concert.builder()
                .concertId(1L)
                .name("싸이 흠뻑쇼")
                .concertDates(List.of(ConcertDate.builder()
                        .place(Place.builder()
                                .name("올림픽 경기장").build())
                        .build()))
                .build();

        when(concertFacade.getConcert(concertId)).thenReturn(response);

        //when //then
        mockMvc.perform(get("/v1/concerts/%s".formatted(concertId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty());

    }

    @Test
    @DisplayName("예약 가능한 콘서트 날짜를 조회한다.")
    void getConcertDates() throws Exception {
        //given
        Long concertId = 1L;
        List<ConcertDate> response = List.of(ConcertDate.builder()
                .concertDate("2024-09-24")
                .isAvailable(true)
                .place(Place.builder()
                        .name("올림픽 경기장").build())
                .build());

        when(concertFacade.getAvailableConcertDates(concertId)).thenReturn(response);

        //when //then
        mockMvc.perform(get("/v1/concerts/%s/dates".formatted(concertId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("OK"))
                .andExpect(jsonPath("$.data").isArray());

    }

    @Test
    @DisplayName("예약 가능한 콘서트 날짜가 없으면 success를 false로 반환한다.")
    void getConcertDatesWithUnavailableDate() throws Exception {
        //given
        Long concertId = 1L;

        when(concertFacade.getAvailableConcertDates(concertId)).thenThrow(
                new CustomException(CONCERT_DATE_IS_NOT_FOUND,
                        CONCERT_DATE_IS_NOT_FOUND.getMsg())
        );

        //when //then
        mockMvc.perform(get("/v1/concerts/%s/dates".formatted(concertId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(false));

    }

    @Test
    @DisplayName("예약 가능한 콘서트 좌석을 조회한다.")
    void getSeats() throws Exception {
        //given
        Long concertDateId = 1L;
        List<Seat> response = List.of(Seat.builder()
                .seatNumber(49).build());

        when(concertFacade.getAvailableSeats(concertDateId)).thenReturn(response);

        //when //then
        mockMvc.perform(get("/v1/concerts/dates/%s/seats".formatted(concertDateId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("OK"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("예약 가능한 콘서트 좌석이 없으면 success를 false로 반환한다.")
    void getSeatsWithUnAvailableSeat() throws Exception {
        //given
        Long concertDateId = 1L;

        //when
        when(concertFacade.getAvailableSeats(concertDateId)).thenThrow(
                new CustomException(SEAT_IS_NOT_FOUND,
                        SEAT_IS_NOT_FOUND.getMsg())
        );

        //then
        mockMvc.perform(get("/v1/concerts/dates/%s/seats".formatted(concertDateId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.success").value(false));
    }
}