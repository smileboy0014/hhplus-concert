package com.hhplus.hhplusconcert.application;

import com.hhplus.hhplusconcert.application.queue.WaitingQueueFacade;
import com.hhplus.hhplusconcert.domain.queue.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.WaitingQueueService;
import com.hhplus.hhplusconcert.domain.queue.command.WaitingQueueCommand;
import com.hhplus.hhplusconcert.domain.user.User;
import com.hhplus.hhplusconcert.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WaitingQueueFacadeTest {

    @Mock
    WaitingQueueService waitingQueueService;
    @Mock
    UserService userService;
    @InjectMocks
    WaitingQueueFacade waitingQueueFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("토큰을 발급하고, 대기열 정보를 확인하는 유즈케이스를 실행한다.")
    void checkWaiting() {
        // given
        WaitingQueueCommand.Create command = new WaitingQueueCommand.Create(1L, "jwt-token");
        User user = User.builder()
                .userId(command.userId())
                .build();

        WaitingQueue queue = WaitingQueue.builder()
                .status(WaitingQueue.WaitingQueueStatus.ACTIVE).build();

        when(userService.getUser(command.userId())).thenReturn(user);
        when(waitingQueueService.checkWaiting(any(User.class), any(String.class))).thenReturn(queue);

        // when
        WaitingQueue result = waitingQueueFacade.checkWaiting(command);

        // then
        assertThat(result.getStatus()).isEqualTo(WaitingQueue.WaitingQueueStatus.ACTIVE);
    }

    @Test
    @DisplayName("대기열에 있는 토큰을 순차적으로 active 시키는 유즈케이스를 실행한다.")
    void active() {
        //given //when
        waitingQueueService.activeTokens();

        //then
        verify(waitingQueueService).activeTokens();
    }

}