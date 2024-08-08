package com.hhplus.hhplusconcert.domain.queue;

import com.hhplus.hhplusconcert.domain.user.User;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface WaitingQueueRepository {

    Optional<WaitingQueue> saveQueue(WaitingQueue queue);

    long getActiveCnt();

    Set<String> getWaitingTokens();

    void saveActiveQueue(User user, String token);

    void deleteWaitingQueue(User user, String token);

    Long getMyWaitingNum(User user, String token);

    void saveWaitingQueue(User user, String token);

    void deleteWaitingTokens();

    void saveActiveQueues(Set<String> waitingTokens);

    void deleteExpiredToken(String token);

    void setTimeout(String key, long timeout, TimeUnit unit);
}
