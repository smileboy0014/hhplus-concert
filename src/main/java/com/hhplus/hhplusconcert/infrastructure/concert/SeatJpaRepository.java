package com.hhplus.hhplusconcert.infrastructure.concert;

import com.hhplus.hhplusconcert.domain.concert.entity.Seat;
import com.hhplus.hhplusconcert.domain.concert.enums.SeatStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {

    List<Seat> findAllByConcertDateInfo_concertDateIdAndStatus(Long concertDateId, SeatStatus status);


    Optional<Seat> findByConcertDateInfo_concertDateIdAndSeatNumber(Long concertDateId, int seatNumber);

    //비관적 락 사용
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.concertDateInfo.concertDateId = :concertDateId AND s.seatNumber = :seatNumber")
    Optional<Seat> findSeatWithPessimisticLock(@Param("concertDateId") Long concertDateId,
                                               @Param("seatNumber") int seatNumber);


    boolean existsByConcertDateInfo_concertDateIdAndStatus(Long concertDateId, SeatStatus status);

    void deleteAllInBatch();
}
