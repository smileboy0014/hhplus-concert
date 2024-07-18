package com.hhplus.hhplusconcert.infrastructure.concert;


import com.hhplus.hhplusconcert.domain.concert.entity.Reservation;
import com.hhplus.hhplusconcert.domain.concert.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByUserId(Long userId);

    List<Reservation> findAllByStatusIs(ReservationStatus status);

    boolean existsByConcertDateIdAndSeatNumberAndStatusIsNot(Long concertDateId, int seatNumber, ReservationStatus status);

}
