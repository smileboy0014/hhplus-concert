package com.hhplus.hhplusconcert.infrastructure.queue;

import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WaitingQueueJpaRepository extends JpaRepository<WaitingQueue, Long> {

    List<WaitingQueue> findAllByStatusIsOrderByRequestTime(WaitingQueueStatus status);

    Optional<WaitingQueue> findByUser_userIdAndToken(Long userId, String token);

    WaitingQueue findByUser_UserIdAndStatusIs(Long userId, WaitingQueueStatus status);

    Optional<WaitingQueue> findByUser_userIdAndStatusIsNot(Long userId, WaitingQueueStatus status);

    Optional<WaitingQueue> findByToken(String token);

    long countByStatusIs(WaitingQueueStatus status);

    void deleteAllByStatusIs(WaitingQueueStatus status);

    List<WaitingQueue> findAllByStatusIs(WaitingQueueStatus status);
}
