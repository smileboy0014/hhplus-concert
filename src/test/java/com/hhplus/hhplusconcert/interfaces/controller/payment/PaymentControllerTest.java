package com.hhplus.hhplusconcert.interfaces.controller.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.hhplusconcert.application.payment.PaymentFacade;
import com.hhplus.hhplusconcert.domain.payment.enums.PaymentStatus;
import com.hhplus.hhplusconcert.domain.payment.service.dto.PaymentInfo;
import com.hhplus.hhplusconcert.interfaces.controller.payment.dto.PayRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PaymentFacade paymentFacade;

    @Test
    @DisplayName("결제를 요청한다.")
    void pay() throws Exception {
        // given
        PayRequest request = PayRequest
                .builder()
                .reservationId(1L)
                .userId(1L)
                .build();

        PaymentInfo response = PaymentInfo
                .builder()
                .paymentId(1L)
                .status(PaymentStatus.COMPLETE)
                .build();

        when(paymentFacade.pay(request.toServiceRequest())).thenReturn(response);

        // when // then
        mockMvc.perform(post("/v1/payments/pay")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("OK"));
    }
}