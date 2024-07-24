package com.hhplus.hhplusconcert.domain.queue;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WaitingQueueRepository {
    List<WaitingQueue> getTokens(Long userId);

    Optional<WaitingQueue> getToken(Long userId, String token);

    Optional<WaitingQueue> getToken(String token);


    Optional<WaitingQueue> saveQueue(WaitingQueue queue);

    long getWaitingCnt();

    long getWaitingCnt(LocalDateTime requestTime);

    long getActiveCnt();

    List<WaitingQueue> getWaitingTokens();

    List<WaitingQueue> getActiveOver10min();

    Optional<WaitingQueue> getActiveToken(Long userId);

    void deleteExpiredTokens();
}
