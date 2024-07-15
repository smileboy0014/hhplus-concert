package com.hhplus.hhplusconcert.infrastructure.queue;

import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import com.hhplus.hhplusconcert.domain.queue.repository.WaitingQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class WaitingQueueRepositoryImpl implements WaitingQueueRepository {

    private final WaitingQueueJpaRepository waitingQueueJpaRepository;

    @Override
    public WaitingQueue save(WaitingQueue waitingQueue) {
        return waitingQueueJpaRepository.save(waitingQueue);
    }

    @Override
    public long countByStatusIs(WaitingQueueStatus status) {
        return waitingQueueJpaRepository.countByStatusIs(status);
    }

    @Override
    public WaitingQueue findByUserIdAndStatusIs(Long userId, WaitingQueueStatus status) {
        return waitingQueueJpaRepository.findByUser_UserIdAndStatusIs(userId, status);
    }

    @Override
    public WaitingQueue findByUserIdAndStatusIsNot(Long userId, WaitingQueueStatus status) {
        return waitingQueueJpaRepository.findByUser_userIdAndStatusIsNot(userId, status);
    }

    @Override
    public WaitingQueue findByToken(String token) {
        return waitingQueueJpaRepository.findByToken(token);
    }

    @Override
    public WaitingQueue findByUserIdAndToken(Long userId, String token) {
        return waitingQueueJpaRepository.findByUser_userIdAndToken(userId, token);
    }

    @Override
    public long countByRequestTimeBeforeAndStatusIs(Timestamp requestTime, WaitingQueueStatus status) {
        return waitingQueueJpaRepository.countByRequestTimeBeforeAndStatusIs(requestTime, status);
    }

    @Override
    public List<WaitingQueue> findAllByStatusIsOrderByRequestTime(WaitingQueueStatus status) {
        return waitingQueueJpaRepository.findAllByStatusIsOrderByRequestTime(status);
    }

    @Override
    public void deleteAllExpireToken() {
        waitingQueueJpaRepository.deleteAllByStatusIs(WaitingQueueStatus.EXPIRED);
    }

    @Override
    public void deleteAll() {
        waitingQueueJpaRepository.deleteAllInBatch();
    }

}
