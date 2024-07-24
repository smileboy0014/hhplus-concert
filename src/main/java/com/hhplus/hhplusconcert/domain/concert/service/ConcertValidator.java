package com.hhplus.hhplusconcert.domain.concert.service;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.concert.entity.ConcertDate;
import com.hhplus.hhplusconcert.domain.concert.entity.Seat;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.*;

@Component
public class ConcertValidator {
    public void validAvailableConcertDates(Long concertId, List<ConcertDate> concertDates) {
        if (concertDates.isEmpty()) throw new CustomException(AVAILABLE_DATE_IS_NOT_FOUND,
                "예약 가능한 콘서트 날짜가 존재하지 않습니다. [concertId : %d]".formatted(concertId));
    }

    public void validAvailableSeats(Long concertDateId, List<Seat> seats) {
        if (seats.isEmpty()) throw new CustomException(SEAT_IS_NOT_FOUND,
                "예약 가능한 좌석이 존재하지 않습니다. [concertDateId : %d]".formatted(concertDateId));
    }

    public void validateExistingReservation(boolean result, Long concertDateId, int seatNumber) {
        if (result) throw new CustomException(RESERVATION_IS_ALREADY_EXISTED,
                "이미 해당 좌석의 예약 내역이 존재합니다. [concertDateId: %d, seatNumber: %d]"
                        .formatted(concertDateId, seatNumber));
    }
}
