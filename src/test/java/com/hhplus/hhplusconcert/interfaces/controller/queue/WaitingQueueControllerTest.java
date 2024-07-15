package com.hhplus.hhplusconcert.interfaces.controller.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.hhplusconcert.application.queue.WaitingQueueFacade;
import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueInfo;
import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueTokenInfo;
import com.hhplus.hhplusconcert.interfaces.controller.queue.dto.WaitingQueueEnterRequest;
import com.hhplus.hhplusconcert.interfaces.controller.queue.dto.WaitingQueueTokenRequest;
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

@WebMvcTest(WaitingQueueController.class)
class WaitingQueueControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    WaitingQueueFacade waitingQueueFacade;

    @Test
    @DisplayName("토큰 발급을 요청한다.")
    void issueToken() throws Exception {
        // given
        WaitingQueueTokenRequest request = WaitingQueueTokenRequest
                .builder()
                .userId(1L)
                .build();

        WaitingQueueTokenInfo response = WaitingQueueTokenInfo
                .builder()
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .build();

        when(waitingQueueFacade.issueToken(request.toServiceRequest())).thenReturn(response);

        // when // then
        mockMvc.perform(post("/v1/queues/issue-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    @DisplayName("대기열에 들어가는 것을 요청한다.")
    void enterWaitingQueue() throws Exception {
        // given
        WaitingQueueEnterRequest request = WaitingQueueEnterRequest
                .builder()
                .userId(1L)
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .build();

        WaitingQueueInfo response = WaitingQueueInfo.builder().build();

        when(waitingQueueFacade.enterQueue(request.toServiceRequest())).thenReturn(response);

        // when // then
        mockMvc.perform(post("/v1/queues/enter")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    @DisplayName("유저의 대기 정보를 요청한다.")
    void checkWaitingQueue() throws Exception {
        // given
        WaitingQueueEnterRequest request = WaitingQueueEnterRequest
                .builder()
                .userId(1L)
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .build();

        // when
        WaitingQueueInfo response = WaitingQueueInfo.builder().build();

        when(waitingQueueFacade.checkQueue(request.toServiceRequest())).thenReturn(response);

        // then
        mockMvc.perform(post("/v1/queues/check")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

}