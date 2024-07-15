package com.hhplus.hhplusconcert.domain.queue.repository;

import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface WaitingQueueRepository {

    // WaitingQueue 관련
    List<WaitingQueue> findAllByStatusIsOrderByRequestTime(String status);

    WaitingQueue findByUserIdAndStatusIsNot(Long userId, String status);

    WaitingQueue findByToken(String token);

    WaitingQueue findByUserIdAndStatusIs(Long userId, String status);

    WaitingQueue findByUserIdAndToken(Long userId, String token);

    WaitingQueue save(WaitingQueue waitingQueue);

    long countByStatusIs(String status);

    long countByRequestTimeBeforeAndStatusIs(Timestamp requestTime, String status);

    void deleteAllExpireToken();

    void deleteAll();

}
