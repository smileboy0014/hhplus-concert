package com.hhplus.hhplusconcert.domain.concert.service;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.concert.entity.Concert;
import com.hhplus.hhplusconcert.domain.concert.entity.ConcertDate;
import com.hhplus.hhplusconcert.domain.concert.entity.Reservation;
import com.hhplus.hhplusconcert.domain.concert.entity.Seat;
import com.hhplus.hhplusconcert.domain.concert.enums.ReservationStatus;
import com.hhplus.hhplusconcert.domain.concert.enums.SeatStatus;
import com.hhplus.hhplusconcert.domain.concert.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class ConcertFinder {

    private final ConcertRepository concertRepository;
    private final ConcertValidator concertValidator;

    public List<Concert> findConcerts() {
        return concertRepository.findAllConcert();
    }

    public Concert findConcertByConcertId(Long concertId) {
        return concertRepository.findConcertByConcertId(concertId)
                .orElseThrow(() -> new CustomException(CONCERT_IS_NOT_FOUND,
                        "콘서트 정보를 찾을 수 없습니다. [ConcertId : %d]".formatted(concertId)));
    }

    public List<ConcertDate> findConcertDates() {
        return concertRepository.findAllConcertDates();
    }

    public List<ConcertDate> findAllConcertDateByConcertId(Long concertId) {
        List<ConcertDate> concertDates = concertRepository.findAllConcertDateByConcertId(concertId);
        concertValidator.validAvailableConcertDates(concertId, concertDates);

        return concertDates;
    }

    public ConcertDate findConcertDateByConcertDateIdAndConcertId(
            Long concertDateId, Long concertId) {
        return concertRepository.findConcertDateByConcertDateIdAndConcertId(concertDateId, concertId)
                .orElseThrow(() -> new CustomException(AVAILABLE_DATE_IS_NOT_FOUND,
                        "예약 가능한 콘서트 날짜가 존재하지 않습니다."));
    }

    public boolean existsConcertDateByConcertId(Long concertId) {
        return concertRepository.existsConcertDateByConcertId(concertId);
    }


    public List<Seat> findAllSeatByConcertDateIdAndStatus(Long concertDateId, SeatStatus status) {
        List<Seat> seats = concertRepository.findAllSeatByConcertDateIdAndStatus(concertDateId, status);
        concertValidator.validAvailableSeats(concertDateId, seats);

        return seats;
    }

    public Seat findSeatBySeatId(Long seatId) {
        return concertRepository.findSeatBySeatId(seatId)
                .orElseThrow(() -> new CustomException(SEAT_IS_NOT_FOUND,
                        "좌석 정보가 존재하지 않습니다. [seatId : %d]".formatted(seatId)));
    }

    public Seat findSeatByConcertDateIdAndSeatNumber(Long concertDateId, int seatNumber) {
        return concertRepository.findSeatByConcertDateIdAndSeatNumber(concertDateId, seatNumber)
                .orElseThrow(() -> new CustomException(RESERVATION_IS_ALREADY_EXISTED,
                        "이미 해당 좌석의 예약 내역이 존재합니다. [seatNumber : %d]".formatted(seatNumber)));
    }

    public Seat findSeatByConcertDateIdAndSeatNumberWithLock(
            Long concertDateId, int seatNumber) {
        return concertRepository.findSeatByConcertDateIdAndSeatNumberWithLock(concertDateId, seatNumber)
                .orElseThrow(() -> new CustomException(RESERVATION_IS_ALREADY_EXISTED,
                        "이미 해당 좌석의 예약 내역이 존재합니다. [seatNumber : %d]".formatted(seatNumber)));
    }


    public boolean existSeatByConcertDateAndStatus(Long concertDateId, SeatStatus seatStatus) {
        return concertRepository.existSeatByConcertDateAndStatus(concertDateId, seatStatus);
    }

    public List<Reservation> findReservations() {
        return concertRepository.findAllReservation();
    }

    public List<Reservation> findAllReservationByUserId(Long userId) {
        return concertRepository.findAllReservationByUserId(userId);
    }

    public List<Reservation> findAllReservationByStatusIs(ReservationStatus status) {
        return concertRepository.findAllReservationByStatusIs(status);
    }

    public Reservation findReservationByReservationId(Long reservationId) {
        return concertRepository.findReservationByReservationId(reservationId)
                .orElseThrow(() -> new CustomException(RESERVATION_IS_NOT_FOUND,
                        "해당 예약 내역을 조회할 수 없습니다. [reservationId: %d]".formatted(reservationId)));
    }

    public void existsReservationByConcertDateIdAndSeatNumber(Long concertDateId, int seatNumber) {
        boolean result = concertRepository.existsReservationByConcertDateIdAndSeatNumberAndStatusIsNot(
                concertDateId, seatNumber, ReservationStatus.CANCEL);

        concertValidator.validateExistingReservation(result, concertDateId, seatNumber);
    }


}
