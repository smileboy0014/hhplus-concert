package com.hhplus.hhplusconcert.infrastructure.concert;

import com.hhplus.hhplusconcert.domain.concert.Seat;
import com.hhplus.hhplusconcert.domain.concert.Seat.SeatStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<SeatEntity, Long> {


    @Query("SELECT s FROM SeatEntity s " +
            "JOIN FETCH s.concertDateInfo cdi " +
            "JOIN FETCH cdi.concertInfo ci " +
            "JOIN FETCH cdi.placeInfo pi " +
            "WHERE cdi.concertDateId = :concertDateId " +
            "AND s.status = :status")
    List<SeatEntity> findAllByConcertDateIdAndStatus(@Param("concertDateId") Long concertDateId,
                                                     @Param("status") SeatStatus status);


    Optional<SeatEntity> findByConcertDateInfo_concertDateIdAndSeatNumber(Long concertDateId, int seatNumber);

    //비관적 락 사용
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SeatEntity s WHERE s.concertDateInfo.concertDateId = :concertDateId AND s.seatNumber = :seatNumber")
    Optional<SeatEntity> findSeatWithPessimisticLock(@Param("concertDateId") Long concertDateId,
                                                     @Param("seatNumber") int seatNumber);


    boolean existsByConcertDateInfo_concertDateIdAndStatus(Long concertDateId, Seat.SeatStatus status);

    void deleteAllInBatch();
}
