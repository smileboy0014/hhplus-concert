package com.hhplus.hhplusconcert.infrastructure.queue;

import com.hhplus.hhplusconcert.domain.queue.WaitingQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.hhplus.hhplusconcert.domain.queue.WaitingQueue.WaitingQueueStatus;

public interface WaitingQueueJpaRepository extends JpaRepository<WaitingQueueEntity, Long> {
    long countByStatusIs(WaitingQueueStatus active);

    List<WaitingQueueEntity> findAllByStatusIsOrderByRequestTime(WaitingQueueStatus status);

    @Query("SELECT w FROM WaitingQueueEntity w JOIN FETCH w.user u WHERE u.userId = :userId AND w.token = :token")
    Optional<WaitingQueueEntity> findByUserIdAndToken(@Param("userId") Long userId,
                                                      @Param("token") String token);

    long countByRequestTimeBeforeAndStatusIs(LocalDateTime requestTime, WaitingQueueStatus wait);

    Optional<WaitingQueueEntity> findByToken(String token);

    @Query("SELECT w FROM WaitingQueueEntity w WHERE w.activeTime <= :timeThreshold AND w.status = :status")
    List<WaitingQueueEntity> getActiveOver10Min(@Param("timeThreshold") LocalDateTime timeThreshold,
                                                @Param("status") WaitingQueue.WaitingQueueStatus status);

    List<WaitingQueueEntity> findAllByUser_userId(Long userId);

    Optional<WaitingQueueEntity> findByUser_userIdAndStatusIs(Long userId, WaitingQueueStatus active);

    void deleteAllInBatchByStatusIs(WaitingQueueStatus expired);
}
