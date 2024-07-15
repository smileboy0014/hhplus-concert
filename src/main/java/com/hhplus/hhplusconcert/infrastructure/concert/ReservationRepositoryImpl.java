package com.hhplus.hhplusconcert.infrastructure.concert;

import com.hhplus.hhplusconcert.domain.common.exception.CustomNotFoundException;
import com.hhplus.hhplusconcert.domain.concert.entity.Reservation;
import com.hhplus.hhplusconcert.domain.concert.enums.ReservationStatus;
import com.hhplus.hhplusconcert.domain.concert.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.RESERVATION_IS_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Reservation reserve(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }

    @Override
    public void deleteAll() {
        reservationJpaRepository.deleteAllInBatch();
    }

    @Override
    public boolean existsByConcertDateIdAndSeatNumberAndStatusIs(Long concertDateId, int seatNumber, ReservationStatus status) {
        return reservationJpaRepository.existsByConcertDateIdAndSeatNumberAndStatusIs(concertDateId, seatNumber, status);
    }

    @Override
    public List<Reservation> findAllByStatusIs(ReservationStatus status) {
        return reservationJpaRepository.findAllByStatusIs(status);
    }

    @Override
    public Reservation findByReservationId(Long reservationId) {
        return reservationJpaRepository.findById(reservationId)
                .orElseThrow(() -> new CustomNotFoundException(RESERVATION_IS_NOT_FOUND,
                        "해당 예약 내역을 조회할 수 없습니다. [reservationId: %d]".formatted(reservationId)));

    }

    @Override
    public List<Reservation> findAllByUserId(Long userId) {
        return reservationJpaRepository.findAllByUserId(userId);
    }

}
