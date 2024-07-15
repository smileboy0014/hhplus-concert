package com.hhplus.hhplusconcert.interfaces.controller.concert;

import com.hhplus.hhplusconcert.application.concert.ConcertFacade;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ConcertDateInfo;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ConcertInfo;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ConcertSeatInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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
        List<ConcertInfo> response = List.of(
                ConcertInfo.builder().build(),
                ConcertInfo.builder().build());

        when(concertFacade.getConcerts()).thenReturn(response);

        //when //then
        mockMvc.perform(get("/v1/concerts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("OK"))
                .andExpect(jsonPath("$.data").isArray());

    }

    @Test
    @DisplayName("콘서트 상세 정보를 조회한다.")
    void getConcert() throws Exception {
        //given
        Long concertId = 1L;
        ConcertInfo response = ConcertInfo.builder().build();

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
        List<ConcertDateInfo> response = List.of(ConcertDateInfo.builder().build());

        when(concertFacade.getConcertDates(concertId)).thenReturn(response);

        //when //then
        mockMvc.perform(get("/v1/concerts/%s/dates".formatted(concertId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("OK"))
                .andExpect(jsonPath("$.data").isArray());

    }

    @Test
    @DisplayName("예약 가능한 콘서트 좌석을 조회한다.")
    void getSeats() throws Exception {
        //given
        Long concertDateId = 1L;
        List<ConcertSeatInfo> response = List.of(ConcertSeatInfo.builder().build());

        when(concertFacade.getAvailableSeats(concertDateId)).thenReturn(response);

        //when //then
        mockMvc.perform(get("/v1/concerts/dates/%s/seats".formatted(concertDateId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("OK"))
                .andExpect(jsonPath("$.data").isArray());
    }
}