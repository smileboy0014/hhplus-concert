package com.hhplus.hhplusconcert.interfaces.controller.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.hhplusconcert.application.reservation.ReservationFacade;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ReservationReserveServiceRequest;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ReservationResponse;
import com.hhplus.hhplusconcert.interfaces.controller.reservation.dto.ReservationReserveRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.hhplus.hhplusconcert.interfaces.controller.reservation.enums.ReservationEnums.SUCCESS_CANCEL_RESERVATION;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationFacade reservationFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("좌석 예약을 요청한다.")
    void reserveSeat() throws Exception {
        // given
        ReservationReserveRequest request = ReservationReserveRequest
                .builder()
                .concertId(1L)
                .concertDateId(1L)
                .seatNumber(11)
                .userId(1L)
                .build();

        ReservationReserveServiceRequest serviceRequest = ReservationReserveServiceRequest
                .builder()
                .concertId(1L)
                .concertDateId(1L)
                .seatNumber(11)
                .userId(1L)
                .build();

        ReservationResponse response = ReservationResponse.builder()
                .reservationId(1L).build();

        when(reservationFacade.reserveSeat(serviceRequest)).thenReturn(response);

        // when // then
        mockMvc.perform(post("/v1/reservations")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("OK"))
                .andExpect(jsonPath("$.data.reservationId").value(1L));

    }

    @Test
    @DisplayName("내 예약 현황을 조회한다.")
    void getReservations() throws Exception {
        // given
        Long userId = 1L;

        when(reservationFacade.getReservations(userId)).thenReturn(List.of(ReservationResponse.builder().build()));

        // when // then
        mockMvc.perform(get("/v1/reservations/%d".formatted(userId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("OK"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("예약 취소를 요청한다.")
    void cancelReservation() throws Exception {
        // given
        Long reservationId = 1L;

        // when // then
        mockMvc.perform(delete("/v1/reservations/%d".formatted(reservationId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("OK"))
                .andExpect(jsonPath("$.data").value(SUCCESS_CANCEL_RESERVATION.getMessage()));

    }


}