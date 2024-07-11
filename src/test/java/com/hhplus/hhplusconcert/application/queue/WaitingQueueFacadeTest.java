package com.hhplus.hhplusconcert.application.queue;

import com.hhplus.hhplusconcert.domain.queue.service.WaitingQueueService;
import com.hhplus.hhplusconcert.domain.queue.service.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class WaitingQueueFacadeTest {

    @Mock
    WaitingQueueService waitingQueueService;

    @InjectMocks
    WaitingQueueFacade waitingQueueFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("토큰 발급 유즈케이스를 실행한다.")
    void issueToken() {
        // given
        WaitingQueueTokenServiceRequest request = WaitingQueueTokenServiceRequest
                .builder()
                .userId(1L)
                .build();
        WaitingQueueTokenResponse response = WaitingQueueTokenResponse
                .builder()
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .build();

        when(waitingQueueService.issueToken(request)).thenReturn(response);

        // when
        WaitingQueueTokenResponse result = waitingQueueFacade.issueToken(request);

        // then
        assertThat(result.token()).isNotEmpty();
    }

    @Test
    @DisplayName("대기열에 들어가는 것을 요청하는 유즈케이스를 실행한다.")
    void enterWaitingQueue() {
        // given
        WaitingQueueEnterServiceRequest request = WaitingQueueEnterServiceRequest
                .builder()
                .userId(1L)
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .build();

        WaitingQueueResponse response = WaitingQueueResponse
                .builder()
                .isActive(false)
                .build();

        when(waitingQueueService.enterQueue(request)).thenReturn(response);

        // when
        WaitingQueueResponse result = waitingQueueFacade.enterQueue(request);

        // then
        assertThat(result.isActive()).isFalse();
    }

    @Test
    @DisplayName("대기열을 확인하는 유즈케이스를 실행한다.")
    void checkWaitingQueue() {
        // given
        WaitingQueueEnterServiceRequest request = WaitingQueueEnterServiceRequest
                .builder()
                .userId(1L)
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .build();

        WaitingQueueResponse response = WaitingQueueResponse
                .builder()
                .waitingInfo(WaitingQueueInfoResponse
                        .builder()
                        .waitingNumber(5L)
                        .build())
                .isActive(false)
                .build();

        when(waitingQueueService.checkQueue(request)).thenReturn(response);

        // when
        WaitingQueueResponse result = waitingQueueFacade.checkQueue(request);

        // then
        assertThat(result)
                .extracting("isActive", "waitingInfo.waitingNumber")
                .containsExactly(false, 5L);

    }

}