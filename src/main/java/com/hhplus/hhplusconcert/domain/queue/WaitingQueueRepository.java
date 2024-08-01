package com.hhplus.hhplusconcert.domain.queue;

import java.util.Optional;
import java.util.Set;

public interface WaitingQueueRepository {

    Optional<WaitingQueue> saveQueue(WaitingQueue queue);

    long getActiveCnt();

    Set<String> getWaitingTokens();

    void deleteExpiredTokens();

    void saveActiveQueue(String token, long expiredTimeMillis);

    void deleteWaitingQueue(String token);

    Long getMyWaitingNum(String token);

    void saveWaitingQueue(String token);

    void deleteWaitingTokens();

    void saveActiveQueues(Set<String> waitingTokens);

    void deleteExpiredToken(String token);

}
