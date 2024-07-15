package com.hhplus.hhplusconcert.domain.queue.repository;

import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface WaitingQueueRepository {

    // WaitingQueue 관련
    List<WaitingQueue> findAllByStatusIsOrderByRequestTime(WaitingQueueStatus status);

    WaitingQueue findByUserIdAndStatusIsNot(Long userId, WaitingQueueStatus status);

    WaitingQueue findByToken(String token);

    WaitingQueue findByUserIdAndStatusIs(Long userId, WaitingQueueStatus status);

    WaitingQueue findByUserIdAndToken(Long userId, String token);

    WaitingQueue save(WaitingQueue waitingQueue);

    long countByStatusIs(WaitingQueueStatus status);

    long countByRequestTimeBeforeAndStatusIs(Timestamp requestTime, WaitingQueueStatus status);

    void deleteAllExpireToken();

    void deleteAll();

}
