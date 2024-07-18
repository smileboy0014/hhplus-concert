package com.hhplus.hhplusconcert.domain.queue.service;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import com.hhplus.hhplusconcert.domain.queue.repository.WaitingQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.NOT_EXIST_IN_WAITING_QUEUE;

@Component
@RequiredArgsConstructor
public class WaitingQueueFinder {
    private final WaitingQueueRepository waitingQueueRepository;
    private final WaitingQueueValidator waitingQueueValidator;

    public List<WaitingQueue> findAllWaitingQueueByStatusIsOrderByRequestTime(WaitingQueueStatus status) {
        return waitingQueueRepository.findAllByStatusIsOrderByRequestTime(status);

    }

    public WaitingQueue findWaitingQueueByUserIdAndStatusIs(Long userId, WaitingQueueStatus status) {
        return waitingQueueRepository.findByUserIdAndStatusIs(userId, status);
    }

    public WaitingQueue findByUserIdAndStatusIsNot(Long userId, WaitingQueueStatus status) {
        return waitingQueueRepository.findByUserIdAndStatusIsNot(userId, status)
                .orElse(null);
    }

    public WaitingQueue findWaitingQueueByUserIdAndToken(Long userId, String token) {
        WaitingQueue waitingQueue = waitingQueueRepository.findByUserIdAndToken(userId, token)
                .orElseThrow(() -> new CustomException(NOT_EXIST_IN_WAITING_QUEUE,
                        "대기열에 토큰이 존재하지 않습니다. 토큰 발급 후, 다시 대기열에 진입해주세요."));
        // 대기열에 있는 토큰 상태 검증
        waitingQueueValidator.ensureIsWaiting(waitingQueue);

        return waitingQueue;
    }

    public long countWaitingQueueByStatusIs(WaitingQueueStatus status) {
        return waitingQueueRepository.countByStatusIs(status);
    }

    public long countWaitingQueueByRequestTimeBeforeAndStatusIs(LocalDateTime requestTime,
                                                                WaitingQueueStatus status) {
        return waitingQueueRepository.countByRequestTimeBeforeAndStatusIs(requestTime, status);
    }


    public WaitingQueue findWaitingQueueByToken(String token) {
        return waitingQueueRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(NOT_EXIST_IN_WAITING_QUEUE,
                        "대기열에 토큰이 존재하지 않습니다. 토큰 발급 후, 다시 대기열에 진입해주세요."));
    }

    public WaitingQueue findWaitingQueueIsActive(Long userId, String token) {
        return waitingQueueRepository.findByUserIdAndToken(userId, token)
                .orElse(null);

    }

    public List<WaitingQueue> getActiveOver10min() {
        return waitingQueueRepository.getActiveOver10min(WaitingQueueStatus.ACTIVE);
    }
}
