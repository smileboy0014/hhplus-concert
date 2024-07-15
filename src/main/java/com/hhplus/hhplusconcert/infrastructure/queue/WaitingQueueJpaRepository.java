package com.hhplus.hhplusconcert.infrastructure.queue;

import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface WaitingQueueJpaRepository extends JpaRepository<WaitingQueue, Long> {

    List<WaitingQueue> findAllByStatusIsOrderByRequestTime(WaitingQueueStatus status);

    WaitingQueue findByUser_userIdAndToken(Long userId, String token);

    WaitingQueue findByUser_UserIdAndStatusIs(Long userId, WaitingQueueStatus status);

    WaitingQueue findByUser_userIdAndStatusIsNot(Long userId, WaitingQueueStatus status);

    WaitingQueue findByToken(String token);

    long countByRequestTimeBeforeAndStatusIs(Timestamp requestTime, WaitingQueueStatus status);

    long countByStatusIs(WaitingQueueStatus status);

    void deleteAllByStatusIs(WaitingQueueStatus status);
}
