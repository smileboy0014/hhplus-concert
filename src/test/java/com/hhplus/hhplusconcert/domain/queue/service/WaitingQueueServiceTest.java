package com.hhplus.hhplusconcert.domain.queue.service;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.common.exception.ErrorCode;
import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import com.hhplus.hhplusconcert.domain.queue.service.dto.*;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.service.UserFinder;
import com.hhplus.hhplusconcert.support.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.*;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class WaitingQueueServiceTest {

    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserFinder userFinder;
    @Mock
    private WaitingQueueAppender waitingQueueAppender;
    @Mock
    private WaitingQueueReader waitingQueueReader;
    @Mock
    private WaitingQueueFinder waitingQueueFinder;
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
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        WaitingQueueTokenServiceRequest request = WaitingQueueTokenServiceRequest
                .builder()
                .userId(1L)
                .build();

        WaitingQueueTokenInfo info = WaitingQueueTokenInfo.of(token);

        when(jwtUtils.createToken(request.userId())).thenReturn(token);
        when(waitingQueueReader.readWaitingQueueToken(token)).thenReturn(info);

        // when
        WaitingQueueTokenInfo result = waitingQueueService.issueToken(request);

        // then
        assertThat(result.token()).isEqualTo(token);
    }

    @Test
    @DisplayName("존재하지 않는 유저로 토큰을 발급 받으려고 하면 USER_IS_NOT_FOUND 예외를 반환한다.")
    void issueTokenNoUser() {
        // given
        WaitingQueueTokenServiceRequest request = WaitingQueueTokenServiceRequest
                .builder()
                .userId(1L)
                .build();

        doThrow(new CustomException(USER_IS_NOT_FOUND,
                "유저 정보가 존재하지 않습니다. [userId : %d]".formatted(request.userId())))
                .when(userFinder).existsUserByUserId(request.userId());

        // when // then
        assertThatThrownBy(() -> waitingQueueService.issueToken(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(USER_IS_NOT_FOUND);
    }

    @Test
    @DisplayName("유저가 대기열에 들어가는 것을 요청하면 대기 정보를 반환한다.")
    void enterWaitingQueue() {
        // given
        WaitingQueueEnterServiceRequest request = WaitingQueueEnterServiceRequest
                .builder()
                .userId(1L)
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .build();

        User user = User.builder().userId(1L).build();

        WaitingQueueInfo info = WaitingQueueInfo.of(false, 1L, WaitingInfo.of(3L, 180));

        when(userFinder.findUserByUserId(request.userId())).thenReturn(user);
        when(waitingQueueFinder.countWaitingQueueByStatusIs(WaitingQueueStatus.ACTIVE)).thenReturn(50L);
        when(waitingQueueFinder.countWaitingQueueByStatusIs(WaitingQueueStatus.WAIT)).thenReturn(2L);
        when(waitingQueueReader.readWaitingQueue(false, request.userId(),
                3L, 180)).thenReturn(info);

        // when
        WaitingQueueInfo result = waitingQueueService.enterQueue(request);

        // then
        assertThat(result)
                .extracting("isActive", "waitingInfo.waitingNumber")
                .containsExactly(false, 3L);

    }

    @Test
    @DisplayName("유저가 대기열에 들어갈 때 대기자가 없다면 토큰이 활성화된 정보를 반환한다.")
    void enterWaitingQueueWithActiveToken() {
        // given
        WaitingQueueEnterServiceRequest request = WaitingQueueEnterServiceRequest
                .builder()
                .userId(1L)
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .build();

        User user = User.builder().userId(1L).build();
        WaitingQueue waitingQueue = WaitingQueue.builder().build();
        WaitingQueueInfo info = WaitingQueueInfo.of(true, 1L, WaitingInfo.builder().build());

        when(userFinder.findUserByUserId(request.userId())).thenReturn(user);
        when(waitingQueueFinder.countWaitingQueueByStatusIs(WaitingQueueStatus.ACTIVE)).thenReturn(45L);
        when(waitingQueueAppender.appendWaitingQueue(request.toEntity(user, WaitingQueueStatus.ACTIVE))).thenReturn(waitingQueue);
        when(waitingQueueReader.readWaitingQueue(true, request.userId(),
                0, 0)).thenReturn(info);

        // when
        WaitingQueueInfo result = waitingQueueService.enterQueue(request);

        // then
        assertThat(result)
                .extracting("isActive", "waitingInfo.waitingNumber")
                .containsExactly(true, 0L);
    }

    @Test
    @DisplayName("유저가 토큰을 통해 대기열을 확인하면 유저의 대기 정보를 반환한다.")
    void checkWaitingQueue() {
        // given
        WaitingQueueEnterServiceRequest request = WaitingQueueEnterServiceRequest
                .builder()
                .userId(1L)
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .build();

        WaitingQueue waitingQueue = WaitingQueue
                .builder()
                .user(User.builder().build())
                .requestTime(now())
                .status(WaitingQueueStatus.WAIT).build();

        WaitingQueueInfo info = WaitingQueueInfo.of(false, 1L, WaitingInfo.of(4L, 240));

        when(waitingQueueFinder.findWaitingQueueByUserIdAndToken(request.userId(), request.token())).thenReturn(waitingQueue);
        when(waitingQueueFinder.countWaitingQueueByRequestTimeBeforeAndStatusIs(waitingQueue.getRequestTime(),
                WaitingQueueStatus.WAIT)).thenReturn(3L);
        when(waitingQueueReader.readWaitingQueue(false, request.userId(),
                4L, 240)).thenReturn(info);

        // when
        WaitingQueueInfo result = waitingQueueService.checkQueue(request);

        // then
        assertThat(result)
                .extracting("isActive", "waitingInfo.waitingNumber")
                .containsExactly(false, 4L);
    }

    @Test
    @DisplayName("대기열에 토큰 정보가 없다면 NOT_EXIST_IN_WAITING_QUEUE 예외를 반환한다.")
    void checkWaitingQueueWithNotExistQueue() {
        // given
        WaitingQueueEnterServiceRequest request = WaitingQueueEnterServiceRequest
                .builder()
                .userId(1L)
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .build();

        doThrow(new CustomException(NOT_EXIST_IN_WAITING_QUEUE,
                "대기열에 토큰이 존재하지 않습니다. 다시 대기열에 진입해주세요."))
                .when(waitingQueueFinder).findWaitingQueueByUserIdAndToken(request.userId(), request.token());

        // when // then
        assertThatThrownBy(() -> waitingQueueService.checkQueue(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(NOT_EXIST_IN_WAITING_QUEUE);

        verify(waitingQueueFinder).findWaitingQueueByUserIdAndToken(request.userId(), request.token());
    }

    @Test
    @DisplayName("대기열에 있는 토큰이 만료되었다면 TOKEN_IS_EXPIRED 예외를 반환한다.")
    void checkWaitingQueueWithNotExistToken() {
        // given
        WaitingQueueEnterServiceRequest request = WaitingQueueEnterServiceRequest
                .builder()
                .userId(1L)
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .build();

        doThrow(new CustomException(TOKEN_IS_EXPIRED,
                "토큰이 만료되었습니다. 다시 발급 후 대기열에 진입해주세요."))
                .when(waitingQueueFinder).findWaitingQueueByUserIdAndToken(request.userId(), request.token());

        // when // then
        assertThatThrownBy(() -> waitingQueueService.checkQueue(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TOKEN_IS_EXPIRED);

        verify(waitingQueueFinder).findWaitingQueueByUserIdAndToken(request.userId(), request.token());
    }

    @Test
    @DisplayName("대기열에 있는 토큰이 활성화 상태라면 ALREADY_TOKEN_IS_ACTIVE 예외를 반환한다.")
    void checkWaitingQueueWithAlreadyActiveToken() {
        // given
        WaitingQueueEnterServiceRequest request = WaitingQueueEnterServiceRequest
                .builder()
                .userId(1L)
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .build();

        doThrow(new CustomException(ErrorCode.ALREADY_TOKEN_IS_ACTIVE,
                "이미 활성화 된 토큰입니다."))
                .when(waitingQueueFinder).findWaitingQueueByUserIdAndToken(request.userId(), request.token());

        // when // then
        assertThatThrownBy(() -> waitingQueueService.checkQueue(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ALREADY_TOKEN_IS_ACTIVE);

        verify(waitingQueueFinder).findWaitingQueueByUserIdAndToken(request.userId(), request.token());
    }

}