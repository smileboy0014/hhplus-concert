package com.hhplus.hhplusconcert.domain.queue;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
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
import java.util.List;
import java.util.Optional;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.ALREADY_TOKEN_IS_ACTIVE;
import static com.hhplus.hhplusconcert.domain.queue.WaitingQueue.WaitingQueueStatus;
import static com.hhplus.hhplusconcert.domain.queue.WaitingQueue.builder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    @DisplayName("유저가 토큰 발급을 요청하면 토큰을 반환한다.")
    void issueToken() {
        // given
        Long userId = 1L;
        when(jwtUtils.createToken(userId)).thenReturn("jwt-token");

        // when
        String result = waitingQueueService.issueToken(userId);

        // then
        assertThat(result).isEqualTo("jwt-token");
    }

    @Test
    @DisplayName("활성 유저가 꽉 차지 않으면 토큰 활성화 정보를 반환한다.")
    void enterQueueWithActiveToken() {
        // given
        User user = User.builder().userId(1L).build();
        String token = "jwt-token";

        WaitingQueue queue = builder()
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
        WaitingQueue result = waitingQueueService.enterQueue(user, token);

        // then
        assertThat(result.getStatus()).isEqualTo(WaitingQueueStatus.ACTIVE);
    }

    @Test
    @DisplayName("활성 유저가 꽉 찼다면 토큰 대기열 정보를 반환한다.")
    void enterQueueWithWaitingToken() {
        // given
        User user = User.builder().userId(1L).build();
        String token = "jwt-token";

        WaitingQueue queue = builder()
                .user(user)
                .token(token)
                .status(WaitingQueueStatus.WAIT)
                .requestTime(LocalDateTime.now())
                .activeTime(LocalDateTime.now())
                .waitingNum(1L)
                .waitTimeInSeconds(Duration.ofMinutes(1L).toSeconds())
                .build();

        when(waitingQueueRepository.getActiveCnt()).thenReturn(50L);
        when(waitingQueueRepository.saveQueue(any(WaitingQueue.class))).thenReturn(Optional.ofNullable(queue));
        when(waitingQueueRepository.getWaitingCnt()).thenReturn(1L);
        when(waitingQueueValidator.checkSavedQueue(any())).thenReturn(queue);

        // when
        WaitingQueue result = waitingQueueService.enterQueue(user, token);

        // then
        assertThat(result.getStatus()).isEqualTo(WaitingQueueStatus.WAIT);
        assertThat(result.getWaitingNum()).isEqualTo(1L);

    }

    @Test
    @DisplayName("토큰을 활성화 시킨다.")
    void activeToken() {
        // given
        User user = User.builder().userId(1L).build();
        String token = "jwt-token";

        WaitingQueue queue = builder()
                .user(user)
                .token(token)
                .status(WaitingQueueStatus.WAIT)
                .requestTime(LocalDateTime.now())
                .activeTime(LocalDateTime.now())
                .waitingNum(1L)
                .waitTimeInSeconds(Duration.ofMinutes(1L).toSeconds())
                .build();

        when(waitingQueueRepository.getActiveCnt()).thenReturn(49L);
        when(waitingQueueRepository.getWaitingTokens()).thenReturn(List.of(queue));

        // when
        waitingQueueService.activeToken(null);

        // then
        verify(waitingQueueRepository).saveQueue(queue);
        assertThat(queue.getStatus()).isEqualTo(WaitingQueueStatus.ACTIVE);
    }

    @Test
    @DisplayName("유저의 대기열 정보를 반환한다.")
    void checkQueue() {
        // given
        User user = User.builder().userId(1L).build();
        String token = "jwt-token";

        WaitingQueue queue = builder()
                .user(user)
                .token(token)
                .status(WaitingQueueStatus.WAIT)
                .requestTime(LocalDateTime.now())
                .activeTime(LocalDateTime.now())
                .waitingNum(1L)
                .waitTimeInSeconds(Duration.ofMinutes(1L).toSeconds())
                .build();

        when(waitingQueueRepository.getToken(user.getUserId(), token)).thenReturn(Optional.ofNullable(queue));
        when(waitingQueueRepository.getWaitingCnt(any(LocalDateTime.class))).thenReturn(3L);
        // when
        WaitingQueue result = waitingQueueService.checkQueue(user.getUserId(), token);

        // then
        assertThat(result.getWaitingNum()).isEqualTo(3L);

    }

    @Test
    @DisplayName("이미 토큰이 활성 상태라면 ALREADY_TOKEN_IS_ACTIVE 예외를 반환한다.")
    void checkQueueWithAlreadyActiveStatus() {
        // given
        User user = User.builder().userId(1L).build();
        String token = "jwt-token";

        WaitingQueue queue = builder()
                .user(user)
                .token(token)
                .status(WaitingQueueStatus.ACTIVE)
                .requestTime(LocalDateTime.now())
                .activeTime(LocalDateTime.now())
                .build();

        when(waitingQueueRepository.getToken(user.getUserId(), token)).thenReturn(Optional.ofNullable(queue));

        // when // then
        assertThatThrownBy(() -> waitingQueueService.checkQueue(user.getUserId(), token))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ALREADY_TOKEN_IS_ACTIVE);

    }

    @Test
    @DisplayName("토큰이 active 된지 정의된 시간 보다 넘었다면 expired 시킨다.")
    void expiredToken() {
        // given
        User user = User.builder().userId(1L).build();
        String token = "jwt-token";

        WaitingQueue queue = builder()
                .user(user)
                .token(token)
                .status(WaitingQueueStatus.EXPIRED)
                .requestTime(LocalDateTime.now())
                .activeTime(LocalDateTime.now().minusMinutes(10).plusSeconds(1))
                .waitingNum(1L)
                .waitTimeInSeconds(Duration.ofMinutes(1L).toSeconds())
                .build();

        when(waitingQueueRepository.getActiveOver10min()).thenReturn(List.of(queue));
        // when
        waitingQueueService.expireToken();

        // then
        assertThat(queue.getStatus()).isEqualTo(WaitingQueueStatus.EXPIRED);
    }

    @Test
    @DisplayName("강제로 토큰을 만료시킨다.")
    void forceExpireToken() {
        // given
        Long userId = 1L;

        WaitingQueue queue = builder()
                .status(WaitingQueueStatus.ACTIVE)
                .requestTime(LocalDateTime.now())
                .activeTime(LocalDateTime.now().minusMinutes(10).plusSeconds(1))
                .waitingNum(1L)
                .waitTimeInSeconds(Duration.ofMinutes(1L).toSeconds())
                .build();

        when(waitingQueueRepository.getActiveToken(userId)).thenReturn(Optional.ofNullable(queue));

        // when
        waitingQueueService.forceExpireToken(userId);

        // then
        assertThat(queue.getStatus()).isEqualTo(WaitingQueueStatus.EXPIRED);
    }

}