package com.hhplus.hhplusconcert.infrastructure.queue;

import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface WaitingQueueJpaRepository extends JpaRepository<WaitingQueue, Long> {

    List<WaitingQueue> findAllByStatusIs(String status);

    List<WaitingQueue> findAllByStatusIsOrderByRequestTime(String status);

    List<WaitingQueue> findAllByRequestTimeBeforeAndStatusIs(Timestamp requestTime, String status);

    WaitingQueue findByUser_userIdAndToken(Long userId, String token);

    WaitingQueue findByUser_UserIdAndStatusIs(Long userId, String status);

    WaitingQueue findByUser_userIdAndStatusIsNot(Long userId, String status);

    WaitingQueue findByToken(String token);

    long countByRequestTimeBeforeAndStatusIs(Timestamp requestTime, String status);

    long countByStatusIs(String status);

    void deleteAllByStatusIs(String status);
}
