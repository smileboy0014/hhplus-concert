package com.hhplus.hhplusconcert.infrastructure.queue;

import com.hhplus.hhplusconcert.domain.queue.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.WaitingQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.hhplus.hhplusconcert.domain.queue.WaitingQueue.WaitingQueueStatus;

@Repository
@RequiredArgsConstructor
public class WaitingQueueRepositoryImpl implements WaitingQueueRepository {

    private final WaitingQueueJpaRepository waitingQueueJpaRepository;

    @Override
    public List<WaitingQueue> getTokens(Long userId) {
        List<WaitingQueueEntity> queueEntities = waitingQueueJpaRepository.findAllByUser_userId(userId);

        return queueEntities.stream()
                .map(WaitingQueueEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<WaitingQueue> getToken(Long userId, String token) {
        Optional<WaitingQueueEntity> queueEntity = waitingQueueJpaRepository.findByUserIdAndToken(userId, token);

        if (queueEntity.isPresent()) {
            return queueEntity.map(WaitingQueueEntity::toDomain);
        }
        return Optional.empty();
    }

    @Override
    public Optional<WaitingQueue> getToken(String token) {
        Optional<WaitingQueueEntity> queueEntity = waitingQueueJpaRepository.findByToken(token);

        if (queueEntity.isPresent()) {
            return queueEntity.map(WaitingQueueEntity::toDomain);
        }
        return Optional.empty();
    }

    @Override
    public Optional<WaitingQueue> saveQueue(WaitingQueue queue) {
        WaitingQueueEntity queueEntity = waitingQueueJpaRepository.save(WaitingQueueEntity.toEntity(queue));

        return Optional.of(queueEntity.toDomain());
    }

    @Override
    public long getActiveCnt() {
        return waitingQueueJpaRepository.countByStatusIs(WaitingQueueStatus.ACTIVE);
    }

    @Override
    public List<WaitingQueue> getWaitingTokens() {
        List<WaitingQueueEntity> queueEntities = waitingQueueJpaRepository
                .findAllByStatusIsOrderByRequestTime(WaitingQueueStatus.WAIT);

        return queueEntities.stream()
                .map(WaitingQueueEntity::toDomain)
                .toList();

    }

    @Override
    public List<WaitingQueue> getActiveOver10min() {
        List<WaitingQueueEntity> queueEntity = waitingQueueJpaRepository
                .getActiveOver10Min(LocalDateTime.now().minusMinutes(10), //10분이 지났는지
                        WaitingQueueStatus.ACTIVE);

        return queueEntity.stream().map(WaitingQueueEntity::toDomain).toList();

    }

    @Override
    public Optional<WaitingQueue> getActiveToken(Long userId) {
        Optional<WaitingQueueEntity> activeToken = waitingQueueJpaRepository.
                findByUser_userIdAndStatusIs(userId, WaitingQueueStatus.ACTIVE);

        if (activeToken.isPresent()) {
            return activeToken.map(WaitingQueueEntity::toDomain);
        }

        return Optional.empty();
    }

    @Override
    public void deleteExpiredTokens() {
        waitingQueueJpaRepository.deleteAllInBatchByStatusIs(WaitingQueueStatus.EXPIRED);
    }

    @Override
    public long getWaitingCnt() {
        return waitingQueueJpaRepository.countByStatusIs(WaitingQueueStatus.WAIT);
    }

    @Override
    public long getWaitingCnt(LocalDateTime requestTime) {
        return waitingQueueJpaRepository.countByRequestTimeBeforeAndStatusIs(requestTime,
                WaitingQueueStatus.WAIT);
    }
}
