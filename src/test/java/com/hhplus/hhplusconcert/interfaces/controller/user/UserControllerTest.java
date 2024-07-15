package com.hhplus.hhplusconcert.interfaces.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.hhplusconcert.application.user.UserFacade;
import com.hhplus.hhplusconcert.domain.user.service.dto.UserResponse;
import com.hhplus.hhplusconcert.interfaces.controller.user.dto.UserBalanceRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserFacade userFacade;

    @Test
    @DisplayName("유저 잔액을 요청한다.")
    void getBalance() throws Exception {
        // given
        Long userId = 1L;
        UserResponse response = UserResponse.builder().build();

        when(userFacade.getBalance(userId)).thenReturn(response);

        // when // then
        mockMvc.perform(get("/v1/users/%d/balance".formatted(userId)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    @DisplayName("유저 잔액 충전을 요청한다.")
    void chargeBalance() throws Exception {
        // given
        Long userId = 1L;
        BigDecimal amount = BigDecimal.valueOf(20000);
        UserBalanceRequest request = UserBalanceRequest.builder()
                .balance(amount)
                .build();

        UserResponse response = UserResponse.builder().balance(amount).build();

        when(userFacade.chargeBalance(request.toServiceRequest(userId))).thenReturn(response);

        // when // then
        mockMvc.perform(patch("/v1/users/%d/charge".formatted(userId))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("OK"))
                .andExpect(jsonPath("$.data.balance").value(amount));

    }

}