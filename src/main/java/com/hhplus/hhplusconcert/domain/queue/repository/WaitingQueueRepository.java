package com.hhplus.hhplusconcert.domain.queue.repository;

import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WaitingQueueRepository {

    // WaitingQueue 관련
    List<WaitingQueue> findAllByStatusIsOrderByRequestTime(WaitingQueueStatus status);

    Optional<WaitingQueue> findByUserIdAndStatusIsNot(Long userId, WaitingQueueStatus status);

    Optional<WaitingQueue> findByToken(String token);

    WaitingQueue findByUserIdAndStatusIs(Long userId, WaitingQueueStatus status);

    Optional<WaitingQueue> findByUserIdAndToken(Long userId, String token);

    WaitingQueue save(WaitingQueue waitingQueue);

    long countByStatusIs(WaitingQueueStatus status);

    long countByRequestTimeBeforeAndStatusIs(LocalDateTime requestTime, WaitingQueueStatus status);

    void deleteAllExpireToken();

    void deleteAll();

    List<WaitingQueue> getActiveOver10min(WaitingQueueStatus status);
}
