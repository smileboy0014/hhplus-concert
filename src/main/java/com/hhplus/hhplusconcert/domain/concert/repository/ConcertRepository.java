package com.hhplus.hhplusconcert.domain.concert.repository;

import com.hhplus.hhplusconcert.domain.concert.entity.Concert;
import com.hhplus.hhplusconcert.domain.concert.entity.ConcertDate;
import com.hhplus.hhplusconcert.domain.concert.entity.Seat;
import com.hhplus.hhplusconcert.domain.concert.enums.SeatStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConcertRepository {

    // Concert 관련
    List<Concert> findAllConcert();

    Concert findConcertByConcertId(Long concertId);

    Concert addConcert(Concert concert);

    // ConcertDate 관련
    List<ConcertDate> addConcertDates(List<ConcertDate> concertDates);

    List<ConcertDate> findAllConcertDateByConcertId(Long concertId);

    List<ConcertDate> findAllConcertDates();

    ConcertDate findConcertDateByConcertDateIdAndConcertId(Long concertDateId, Long concertId);

    boolean existsConcertDateByConcertId(Long concertId);

    // Seat 관련
    List<Seat> addSeats(List<Seat> seat);

    List<Seat> findAllSeatByConcertDateIdAndStatus(Long concertDateId, SeatStatus status);

    Seat findSeatBySeatId(Long seatId);

    Seat findBySeatConcertDateIdAndSeatNumber(Long concertDateId, int seatNumber);

    boolean existSeatByConcertDateAndStatus(Long concertId, SeatStatus status);

    // Common
    void deleteAll();

}
