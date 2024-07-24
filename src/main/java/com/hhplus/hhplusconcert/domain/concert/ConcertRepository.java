package com.hhplus.hhplusconcert.domain.concert;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConcertRepository {

    List<Concert> getConcerts();

    List<ConcertDate> getConcertDates(Long concertId);

    boolean existAvailableSeats(Long concertDateId);

    List<Seat> getAvailableSeats(Long concertDateId);

    Optional<Place> savePlace(Place place);

    Optional<Concert> saveConcert(Concert concert1);

    List<ConcertDate> saveConcertDates(List<ConcertDate> concertDates);

    List<Seat> saveSeats(List<Seat> seats);


    void deleteAll();

    boolean checkAlreadyReserved(Long concertId, Long concertDateId, int seatNumber);

    Optional<ConcertDate> getDateForReservation(Long concertDateId, Long concertId);

    Optional<Seat> getSeatForReservation(Long concertDateId, int seatNumber);

    Optional<ConcertReservationInfo> saveReservation(ConcertReservationInfo build);

    List<ConcertReservationInfo> getMyReservations(Long userId);

    Optional<ConcertReservationInfo> getReservation(Long reservationId);


    Optional<Seat> getSeat(Long seatId);

    Optional<Seat> saveSeat(Seat seat);

    List<ConcertReservationInfo> getAllTempReservation();

}
