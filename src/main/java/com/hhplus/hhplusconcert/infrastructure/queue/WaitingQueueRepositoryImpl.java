package com.hhplus.hhplusconcert.infrastructure.queue;

import com.hhplus.hhplusconcert.domain.queue.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.WaitingQueueRepository;
import com.hhplus.hhplusconcert.domain.user.User;
import com.hhplus.hhplusconcert.infrastructure.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.hhplus.hhplusconcert.domain.queue.WaitingQueueConstants.*;

@Repository
@RequiredArgsConstructor
public class WaitingQueueRepositoryImpl implements WaitingQueueRepository {

    private final WaitingQueueJpaRepository waitingQueueJpaRepository;
    private final RedisRepository redisRepository;

    @Override
    public Optional<WaitingQueue> saveQueue(WaitingQueue queue) {
        WaitingQueueEntity queueEntity = waitingQueueJpaRepository.save(WaitingQueueEntity.toEntity(queue));

        return Optional.of(queueEntity.toDomain());
    }

    @Override
    public long getActiveCnt() {
        return redisRepository.countActiveTokens();
    }

    @Override
    public void saveActiveQueue(User user, String token) {
        redisRepository.setAdd(ACTIVE_KEY + ":" + token, String.valueOf(user.getUserId()));
    }

    @Override
    public void setTimeout(String key, long timeout, TimeUnit unit) {
        redisRepository.setTtl(ACTIVE_KEY + ":" + key, timeout, unit);
    }

    @Override
    public void deleteWaitingQueue(User user, String token) {
        redisRepository.zSetRemove(WAIT_KEY, token + ":" + user.getUserId());
    }

    @Override
    public Long getMyWaitingNum(User user, String token) {
        return redisRepository.zSetRank(WAIT_KEY, token + ":" + user.getUserId());
    }

    @Override
    public void saveWaitingQueue(User user, String token) {
        redisRepository.zSetAdd(WAIT_KEY, token + ":" + user.getUserId(), System.currentTimeMillis());
    }

    @Override
    public Set<String> getWaitingTokens() {
        return redisRepository.zSetGetRange(WAIT_KEY, 0, ENTER_10_SECONDS - 1);
    }

    @Override
    public void deleteWaitingTokens() {
        redisRepository.zSetRemoveRange(WAIT_KEY, 0, ENTER_10_SECONDS - 1);
    }

    @Override
    public void saveActiveQueues(Set<String> tokens) {
        redisRepository.setAddRangeWithTtl(ACTIVE_KEY, tokens, AUTO_EXPIRED_TIME, TimeUnit.MILLISECONDS);
    }

    @Override
    public void deleteExpiredToken(String token) {
        redisRepository.deleteKey(ACTIVE_KEY + ":" + token);
    }

}
