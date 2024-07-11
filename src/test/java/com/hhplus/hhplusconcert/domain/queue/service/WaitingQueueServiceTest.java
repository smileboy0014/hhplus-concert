package com.hhplus.hhplusconcert.domain.queue.service;

import com.hhplus.hhplusconcert.common.utils.JwtUtils;
import com.hhplus.hhplusconcert.domain.common.exception.CustomBadRequestException;
import com.hhplus.hhplusconcert.domain.common.exception.CustomNotFoundException;
import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import com.hhplus.hhplusconcert.domain.queue.repository.WaitingQueueRepository;
import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueEnterServiceRequest;
import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueResponse;
import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueTokenResponse;
import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueTokenServiceRequest;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.NOT_EXIST_IN_WAITING_QUEUE;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.USER_IS_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class WaitingQueueServiceTest {

    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private WaitingQueueRepository waitingQueueRepository;
    @Mock
    private UserRepository userRepository;
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

        when(userRepository.existsByUserId(request.userId())).thenReturn(true);
        when(jwtUtils.createToken(request.userId())).thenReturn(token);

        // when
        WaitingQueueTokenResponse result = waitingQueueService.issueToken(request);

        // then
        assertThat(result.token()).isEqualTo(token);
    }

    @Test
    @DisplayName("존재하지 않는 유저로 토큰을 발급 받으려고 하면 USER_IS_NOT_FOUND 예외를 반환한다.")
    void issueTokenNoUser() {
        // given
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        WaitingQueueTokenServiceRequest request = WaitingQueueTokenServiceRequest
                .builder()
                .userId(1L)
                .build();

        when(userRepository.existsByUserId(request.userId())).thenReturn(false);


        // when // then
        assertThatThrownBy(() -> waitingQueueService.issueToken(request))
                .isInstanceOf(CustomNotFoundException.class)
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

        when(userRepository.findUserByUserId(request.userId())).thenReturn(user);
        when(waitingQueueRepository.countByStatusIs(WaitingQueueStatus.ACTIVE.getStatus())).thenReturn(50L);
        when(waitingQueueRepository.countByStatusIs(WaitingQueueStatus.WAIT.getStatus())).thenReturn(2L);

        // when
        WaitingQueueResponse result = waitingQueueService.enterQueue(request);

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


        when(userRepository.findUserByUserId(request.userId())).thenReturn(user);
        when(waitingQueueRepository.countByStatusIs(WaitingQueueStatus.ACTIVE.getStatus())).thenReturn(45L);
        when(waitingQueueRepository.save(request.toEntity(user, WaitingQueueStatus.ACTIVE.getStatus()))).thenReturn(waitingQueue);

        // when
        WaitingQueueResponse result = waitingQueueService.enterQueue(request);

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
                .requestTime(new Timestamp(System.currentTimeMillis()))
                .status(WaitingQueueStatus.WAIT.getStatus()).build();

        when(waitingQueueRepository.findByUserIdAndToken(request.userId(), request.token())).thenReturn(waitingQueue);
        when(waitingQueueRepository.countByRequestTimeBeforeAndStatusIs(waitingQueue.getRequestTime(), WaitingQueueStatus.WAIT.getStatus())).thenReturn(3L);

        // when
        WaitingQueueResponse result = waitingQueueService.checkQueue(request);

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

        when(waitingQueueRepository.findByUserIdAndToken(request.userId(), request.token())).thenReturn(null);

        // when // then
        assertThatThrownBy(() -> waitingQueueService.checkQueue(request))
                .isInstanceOf(CustomBadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(NOT_EXIST_IN_WAITING_QUEUE);
    }

}