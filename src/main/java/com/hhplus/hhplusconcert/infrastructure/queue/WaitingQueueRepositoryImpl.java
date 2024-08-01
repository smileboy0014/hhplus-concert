package com.hhplus.hhplusconcert.infrastructure.queue;

import com.hhplus.hhplusconcert.domain.queue.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.WaitingQueueRepository;
import com.hhplus.hhplusconcert.infrastructure.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

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
        return redisRepository.zSetSize(ACTIVE_KEY);
    }

    @Override
    public void deleteExpiredTokens() {
        redisRepository.zSetRemoveRangeByScore(ACTIVE_KEY, 0, System.currentTimeMillis());
    }

    @Override
    public void saveActiveQueue(String token, long expiredTimeMillis) {
        redisRepository.zSetAdd(ACTIVE_KEY, token, expiredTimeMillis);
    }

    @Override
    public void deleteWaitingQueue(String token) {
        redisRepository.zSetRemove(WAIT_KEY, token);
    }

    @Override
    public Long getMyWaitingNum(String token) {
        return redisRepository.zSetRank(WAIT_KEY, token);
    }

    @Override
    public void saveWaitingQueue(String token) {
        redisRepository.zSetAdd(WAIT_KEY, token, System.currentTimeMillis());
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
        redisRepository.zSetAddRange(ACTIVE_KEY, tokens);
    }

    @Override
    public void deleteExpiredToken(String token) {
        redisRepository.zSetRemove(ACTIVE_KEY, token);
    }
}
