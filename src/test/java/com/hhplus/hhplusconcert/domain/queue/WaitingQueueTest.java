package com.hhplus.hhplusconcert.domain.queue;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.ALREADY_TOKEN_IS_ACTIVE;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.TOKEN_IS_NOT_YET;
import static com.hhplus.hhplusconcert.domain.queue.WaitingQueue.WaitingQueueStatus;
import static com.hhplus.hhplusconcert.domain.queue.WaitingQueue.builder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WaitingQueueTest {

    @Test
    @DisplayName("토큰을 만료시킨다.")
    void expired() {
        // given
        WaitingQueue queue = builder()
                .status(WaitingQueueStatus.ACTIVE)
                .build();

        // when
        queue.expire();

        // then
        assertThat(queue.getStatus()).isEqualTo(WaitingQueueStatus.EXPIRED);
    }

    @Test
    @DisplayName("토큰이 활성 상태가 아니면 TOKEN_IS_NOT_YET 예외를 반환한다.")
    void isActive() {
        // given
        WaitingQueue queue = builder()
                .activeTime(LocalDateTime.now().plusMinutes(10).plusSeconds(1))
                .status(WaitingQueueStatus.ACTIVE)
                .build();

        // when // then
        assertThatThrownBy(queue::isActive)
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ALREADY_TOKEN_IS_ACTIVE);
    }

    @Test
    @DisplayName("10분의 만료시간이 지난 토큰을 만료시킨다.")
    void expireOver10min() {
        // given
        WaitingQueue queue = builder()
                .activeTime(LocalDateTime.now().plusMinutes(9))
                .status(WaitingQueueStatus.ACTIVE)
                .build();

        // when
        queue.expireOver10min();

        // then
        assertThat(queue.getStatus()).isEqualTo(WaitingQueueStatus.EXPIRED);
    }

    @Test
    @DisplayName("10분의 만료시간이 지나지 않았다면 TOKEN_IS_NOT_YET 예외를 반환한다..")
    void expireOver10minNotYet() {
        // given
        WaitingQueue queue = builder()
                .activeTime(LocalDateTime.now().plusMinutes(11))
                .status(WaitingQueueStatus.ACTIVE)
                .build();

        // when // then
        assertThatThrownBy(queue::expireOver10min)
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(TOKEN_IS_NOT_YET);

    }

    @Test
    @DisplayName("토큰을 활성화 시킨다.")
    void active() {
        // given
        WaitingQueue queue = builder()
                .status(WaitingQueueStatus.WAIT)
                .build();

        // when
        queue.active();

        // then
        assertThat(queue.getStatus()).isEqualTo(WaitingQueueStatus.ACTIVE);
    }

    @Test
    @DisplayName("이미 활성화 된 토큰을 활성화 시키면 ALREADY_TOKEN_IS_ACTIVE 예외를 반환한다.")
    void activeAlreadyActiveStatus() {
        // given
        WaitingQueue queue = builder()
                .activeTime(LocalDateTime.now().plusMinutes(9).plusSeconds(59))
                .status(WaitingQueueStatus.ACTIVE)
                .build();

        // when
        assertThatThrownBy(queue::active)
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ALREADY_TOKEN_IS_ACTIVE);
    }

}