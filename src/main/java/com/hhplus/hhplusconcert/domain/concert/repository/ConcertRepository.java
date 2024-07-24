package com.hhplus.hhplusconcert.domain.concert.repository;

import com.hhplus.hhplusconcert.domain.concert.entity.*;
import com.hhplus.hhplusconcert.domain.concert.enums.ReservationStatus;
import com.hhplus.hhplusconcert.domain.concert.enums.SeatStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConcertRepository {

    // Place 관련
    Place addPlace(Place place);

    // Concert 관련
    List<Concert> findAllConcert();

    Optional<Concert> findConcertByConcertId(Long concertId);

    Concert addConcert(Concert concert);

    // ConcertDate 관련
    List<ConcertDate> addConcertDates(List<ConcertDate> concertDates);

    List<ConcertDate> findAllConcertDateByConcertId(Long concertId);

    List<ConcertDate> findAllConcertDates();

    Optional<ConcertDate> findConcertDateByConcertDateIdAndConcertId(Long concertDateId, Long concertId);

    boolean existsConcertDateByConcertId(Long concertId);

    // Seat 관련
    List<Seat> addSeats(List<Seat> seat);

    List<Seat> findAllSeatByConcertDateIdAndStatus(Long concertDateId, SeatStatus status);

    Optional<Seat> findSeatBySeatId(Long seatId);

    Optional<Seat> findSeatByConcertDateIdAndSeatNumber(Long concertDateId, int seatNumber);

    Optional<Seat> findSeatByConcertDateIdAndSeatNumberWithLock(Long concertDateId, int seatNumber);

    boolean existSeatByConcertDateAndStatus(Long concertId, SeatStatus status);

    // Reservation 관련
    List<Reservation> findAllReservation();

    List<Reservation> findAllReservationByUserId(Long userId);

    List<Reservation> findAllReservationByStatusIs(ReservationStatus status);

    Optional<Reservation> findReservationByReservationId(Long reservationId);

    Reservation reserve(Reservation reservation);

    boolean existsReservationByConcertDateIdAndSeatNumberAndStatusIsNot(Long concertDateId, int seatNumber, ReservationStatus status);

    // common
    void deleteAll();


}
