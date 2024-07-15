package com.hhplus.hhplusconcert.infrastructure.concert;

import com.hhplus.hhplusconcert.domain.concert.entity.Seat;
import com.hhplus.hhplusconcert.domain.concert.enums.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {

    List<Seat> findAllByConcertDateInfo_concertDateIdAndStatus(Long concertDateId, SeatStatus status);

    Optional<Seat> findByConcertDateInfo_concertDateIdAndSeatNumber(Long concertDateId, int seatNumber);

    boolean existsByConcertDateInfo_concertDateIdAndStatus(Long concertDateId, SeatStatus status);

    void deleteAllInBatch();
}
