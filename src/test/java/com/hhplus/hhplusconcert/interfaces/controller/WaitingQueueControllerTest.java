package com.hhplus.hhplusconcert.interfaces.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.hhplusconcert.application.queue.WaitingQueueFacade;
import com.hhplus.hhplusconcert.domain.queue.WaitingQueue;
import com.hhplus.hhplusconcert.domain.user.User;
import com.hhplus.hhplusconcert.interfaces.controller.queue.WaitingQueueController;
import com.hhplus.hhplusconcert.interfaces.controller.queue.dto.WaitingQueueDto;
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
    @DisplayName("유저의 대기 정보를 요청한다.")
    void checkWaitingQueue() throws Exception {
        // given
        WaitingQueueDto.Request request = WaitingQueueDto.Request.builder()
                .userId(1L)
                .token("jwt-token")
                .build();

        WaitingQueue waitingQueue = WaitingQueue.builder()
                .user(User.builder().userId(1L).build())
                .token("jwt-token")
                .status(WaitingQueue.WaitingQueueStatus.WAIT)
                .build();

        when(waitingQueueFacade.checkWaiting(request.toCreateCommand())).thenReturn(waitingQueue);

        // then
        mockMvc.perform(post("/v1/queues/token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.msg").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

}