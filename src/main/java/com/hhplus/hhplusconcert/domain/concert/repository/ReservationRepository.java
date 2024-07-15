package com.hhplus.hhplusconcert.domain.concert.repository;

import com.hhplus.hhplusconcert.domain.concert.entity.Reservation;
import com.hhplus.hhplusconcert.domain.concert.enums.ReservationStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository {

    // Reservation 관련
    List<Reservation> findAllByUserId(Long userId);

    List<Reservation> findAllByStatusIs(ReservationStatus status);

    Reservation findByReservationId(Long reservationId);

    Reservation reserve(Reservation reservation);

    boolean existsByConcertDateIdAndSeatNumberAndStatusIs(Long concertDateId, int seatNumber, ReservationStatus status);

    void deleteAll();


}
