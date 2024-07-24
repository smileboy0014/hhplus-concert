package com.hhplus.hhplusconcert.domain.concert;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.*;

@Component
public class ConcertValidator {

    public void existConcert(Long concertId, List<ConcertDate> concertDates) {
        if (concertDates.isEmpty()) throw new CustomException(CONCERT_IS_NOT_FOUND,
                "콘서트 정보를 찾을 수 없습니다. [ConcertId : %d]".formatted(concertId));

    }

    public void existAvailableConcertDates(Long concertId, List<ConcertDate> concertDates) {
        if (concertDates.isEmpty()) throw new CustomException(CONCERT_DATE_IS_NOT_FOUND,
                "예약 가능한 콘서트 날짜가 존재하지 않습니다. [concertId : %d]".formatted(concertId));
    }

    public void existAvailableSeats(Long concertDateId, List<Seat> seats) {
        if (seats.isEmpty()) throw new CustomException(SEAT_IS_NOT_FOUND,
                "예약 가능한 좌석이 존재하지 않습니다. [concertDateId : %d]".formatted(concertDateId));
    }

    public void checkAlreadyReserved(boolean result, Long concertDateId, int seatNumber) {
        if (result) throw new CustomException(RESERVATION_IS_ALREADY_EXISTED,
                "이미 해당 좌석의 예약 내역이 존재합니다. [concertDateId: %d, seatNumber: %d]"
                        .formatted(concertDateId, seatNumber));
    }

    public ConcertDate checkExistConcertDate(Optional<ConcertDate> availableConcertDate, Long concertDateId) {
        if (availableConcertDate.isEmpty()) throw new CustomException(CONCERT_DATE_IS_NOT_FOUND,
                "예약 가능한 콘서트 날짜가 존재하지 않습니다. [concertDate: %d]"
                        .formatted(concertDateId));
        return availableConcertDate.get();

    }

    public Seat checkExistSeat(Optional<Seat> seat, String msg) {
        if (seat.isEmpty()) {
            throw new CustomException(SEAT_IS_NOT_FOUND, msg);
        }
        return seat.get();
    }

    public ConcertReservationInfo checkSavedReservation(Optional<ConcertReservationInfo> concertReservation, String msg) {
        if (concertReservation.isEmpty()) {
            throw new CustomException(RESERVATION_IS_FAILED, msg);
        }
        return concertReservation.get();
    }

    public ConcertReservationInfo checkExistReservation(Optional<ConcertReservationInfo> reservation, String msg) {
        if (reservation.isEmpty()) {
            throw new CustomException(RESERVATION_IS_NOT_FOUND, msg);
        }
        return reservation.get();
    }


}
