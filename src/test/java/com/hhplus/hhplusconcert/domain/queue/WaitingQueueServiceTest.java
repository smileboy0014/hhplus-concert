package com.hhplus.hhplusconcert.domain.queue;

import com.hhplus.hhplusconcert.domain.user.User;
import com.hhplus.hhplusconcert.support.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static com.hhplus.hhplusconcert.domain.queue.WaitingQueue.WaitingQueueStatus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class WaitingQueueServiceTest {

    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private WaitingQueueRepository waitingQueueRepository;
    @Mock
    private WaitingQueueValidator waitingQueueValidator;
    @InjectMocks
    private WaitingQueueService waitingQueueService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("활성 유저가 꽉 차지 않으면 토큰 활성화 정보를 반환한다.")
    void checkWaiting() {
        // given
        User user = User.builder().userId(1L).build();
        String token = "jwt-token";

        WaitingQueue queue = WaitingQueue.builder()
                .user(user)
                .token(token)
                .status(WaitingQueueStatus.ACTIVE)
                .requestTime(LocalDateTime.now())
                .activeTime(LocalDateTime.now())
                .build();

        when(waitingQueueRepository.getActiveCnt()).thenReturn(40L);
        when(waitingQueueRepository.saveQueue(any(WaitingQueue.class))).thenReturn(Optional.ofNullable(queue));
        when(waitingQueueValidator.checkSavedQueue(any())).thenReturn(queue);

        // when
        WaitingQueue result = waitingQueueService.checkWaiting(user, token);

        // then
        assertThat(result.getStatus()).isEqualTo(WaitingQueueStatus.ACTIVE);
    }

    @Test
    @DisplayName("활성 유저가 꽉 찼다면 토큰 대기열 정보를 반환한다.")
    void enterQueueWithWaitingToken() {
        // given
        User user = User.builder().userId(1L).build();
        String token = "jwt-token";

        WaitingQueue queue = WaitingQueue.builder()
                .user(user)
                .token(token)
                .status(WaitingQueueStatus.WAIT)
                .requestTime(LocalDateTime.now())
                .activeTime(LocalDateTime.now())
                .waitingNum(1L)
                .waitTimeInSeconds(Duration.ofMinutes(1L).toSeconds())
                .build();

        when(waitingQueueRepository.getActiveCnt()).thenReturn(1500L);
        when(waitingQueueRepository.saveQueue(any(WaitingQueue.class))).thenReturn(Optional.ofNullable(queue));
        when(waitingQueueValidator.checkSavedQueue(any())).thenReturn(queue);

        // when
        WaitingQueue result = waitingQueueService.checkWaiting(user, token);

        // then
        assertThat(result.getStatus()).isEqualTo(WaitingQueueStatus.WAIT);
        assertThat(result.getWaitingNum()).isEqualTo(0L);

    }

    @Test
    @DisplayName("대기열에 있는 토큰을 활성화 시킨다.")
    void getInActiveQueue() {
        // given
        User user = User.builder().userId(1L).build();
        String token = "jwt-token";

        WaitingQueue queue = WaitingQueue.builder()
                .user(user)
                .token(token)
                .status(WaitingQueueStatus.WAIT)
                .requestTime(LocalDateTime.now())
                .activeTime(LocalDateTime.now())
                .waitingNum(1L)
                .waitTimeInSeconds(Duration.ofMinutes(1L).toSeconds())
                .build();

        Set<String> tokens = Set.of("jwt-token");

        when(waitingQueueRepository.getWaitingTokens()).thenReturn(tokens);

        // when
        waitingQueueService.activeTokens();

        // then
        verify(waitingQueueRepository).deleteWaitingTokens();
        verify(waitingQueueRepository).saveActiveQueues(tokens);
    }

}