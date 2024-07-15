package com.hhplus.hhplusconcert.integration;

import com.hhplus.hhplusconcert.application.queue.WaitingQueueFacade;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.NOT_EXIST_IN_WAITING_QUEUE;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.USER_IS_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class WaitingQueueIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WaitingQueueRepository waitingQueueRepository;

    @Autowired
    private WaitingQueueFacade waitingQueueFacade;

    @BeforeEach
    void setUp() {
        for (int i = 0; i < 49; i++) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            User user = userRepository.addUser(User.builder().build());
            WaitingQueue waitingQueue = WaitingQueue
                    .builder()
                    .user(user)
                    .requestTime(timestamp)
                    .status(WaitingQueueStatus.ACTIVE.getStatus()).build();

            waitingQueueRepository.save(waitingQueue);
        }
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        waitingQueueRepository.deleteAll();
    }

    @Test
    @DisplayName("유저가 토큰 발급을 요청하면 토큰을 반환한다.")
    void issueToken() {
        //given
        User user = userRepository.addUser(User
                .builder()
                .build());

        WaitingQueueTokenServiceRequest request = WaitingQueueTokenServiceRequest
                .builder()
                .userId(user.getUserId())
                .build();

        //when
        WaitingQueueTokenResponse result = waitingQueueFacade.issueToken(request);

        //then
        assertThat(result.token()).isNotEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 유저로 토큰을 발급받으려고 하면 USER_IS_NOT_FOUND 예외를 반환한다.")
    void issueTokenNoUser() {
        //given
        WaitingQueueTokenServiceRequest request = WaitingQueueTokenServiceRequest
                .builder()
                .userId(100000000L)
                .build();

        // when // then
        assertThatThrownBy(() -> waitingQueueFacade.issueToken(request))
                .isInstanceOf(CustomNotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(USER_IS_NOT_FOUND);
    }

    @Test
    @DisplayName("유저가 대기열에 들어가는 것을 요청하면 대기 정보를 반환한다.")
    void enterWaitingQueue() {
        // given
        User user1 = userRepository.addUser(User.builder().build());

        WaitingQueue waitingQueue = WaitingQueue
                .builder()
                .user(user1)
                .requestTime(new Timestamp(System.currentTimeMillis()))
                .status(WaitingQueueStatus.ACTIVE.getStatus()).build();

        waitingQueueRepository.save(waitingQueue);

        User user2 = userRepository.addUser(User.builder().build());


        WaitingQueueEnterServiceRequest request = WaitingQueueEnterServiceRequest
                .builder()
                .userId(user2.getUserId())
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .build();

        // when
        WaitingQueueResponse result = waitingQueueFacade.enterQueue(request);

        // then
        assertThat(result)
                .extracting("isActive", "waitingInfo.waitingNumber")
                .containsExactly(false, 1L);

    }

    @Test
    @DisplayName("유저가 대기열에 들어갈 때 대기자가 없다면 토큰이 활성화된 정보를 반환한다.")
    void enterWaitingQueueWithActiveToken() {
        // given
        User user = userRepository.addUser(User.builder().build());

        WaitingQueueEnterServiceRequest request = WaitingQueueEnterServiceRequest
                .builder()
                .userId(user.getUserId())
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .build();

        // when
        WaitingQueueResponse result = waitingQueueFacade.enterQueue(request);

        // then
        assertThat(result)
                .extracting("isActive", "waitingInfo.waitingNumber")
                .containsExactly(true, 0L);
    }

    @Test
    @DisplayName("유저가 토큰을 통해 대기열을 확인하면 유저의 대기 정보를 반환한다.")
    void checkWaitingQueue() {
        // given
        String tokne = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        User user1 = userRepository.addUser(User.builder().build());

        WaitingQueue waitingQueue1 = WaitingQueue
                .builder()
                .user(user1)
                .requestTime(new Timestamp(System.currentTimeMillis()))
                .status(WaitingQueueStatus.ACTIVE.getStatus()).build();

        waitingQueueRepository.save(waitingQueue1);

        User user2 = userRepository.addUser(User.builder().build());

        WaitingQueue waitingQueue2 = WaitingQueue
                .builder()
                .user(user2)
                .token(tokne)
                .requestTime(new Timestamp(System.currentTimeMillis()))
                .status(WaitingQueueStatus.WAIT.getStatus()).build();

        waitingQueueRepository.save(waitingQueue2);

        WaitingQueueEnterServiceRequest request = WaitingQueueEnterServiceRequest
                .builder()
                .userId(user2.getUserId())
                .token(tokne)
                .build();

        // when
        WaitingQueueResponse result = waitingQueueFacade.checkQueue(request);

        // then
        assertThat(result)
                .extracting("isActive", "waitingInfo.waitingNumber")
                .containsExactly(false, 1L);
    }

    @Test
    @DisplayName("대기열에 토큰 정보가 없다면 NOT_EXIST_IN_WAITING_QUEUE 예외를 반환한다.")
    void checkWaitingQueueWithNotExistQueue() {
        // given
        WaitingQueueEnterServiceRequest request = WaitingQueueEnterServiceRequest
                .builder()
                .userId(100000000L)
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .build();


        // when // then
        assertThatThrownBy(() -> waitingQueueFacade.checkQueue(request))
                .isInstanceOf(CustomBadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(NOT_EXIST_IN_WAITING_QUEUE);
    }
}
