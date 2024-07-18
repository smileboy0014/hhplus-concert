package com.hhplus.hhplusconcert.domain.concert.service;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.concert.entity.Concert;
import com.hhplus.hhplusconcert.domain.concert.entity.ConcertDate;
import com.hhplus.hhplusconcert.domain.concert.entity.Reservation;
import com.hhplus.hhplusconcert.domain.concert.entity.Seat;
import com.hhplus.hhplusconcert.domain.concert.enums.SeatStatus;
import com.hhplus.hhplusconcert.domain.concert.repository.ConcertRepository;
import com.hhplus.hhplusconcert.domain.concert.service.dto.*;
import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import com.hhplus.hhplusconcert.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.PAYMENT_IS_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class ConcertReader {
    private final ConcertRepository concertRepository;
    private final PaymentRepository paymentRepository;

    public List<ConcertInfo> readConcerts(List<Concert> concerts) {
        return concerts.stream()
                .map(concert -> {
                    List<ConcertDate> concertDates = concertRepository.findAllConcertDateByConcertId(concert.getConcertId());
                    return ConcertInfo.of(concert, concertDates);
                })
                .toList();
    }

    public ConcertInfo readConcert(Concert concert, List<ConcertDate> concertDates) {
        return ConcertInfo.of(concert, concertDates);
    }

    public List<ConcertDateInfo> readConcertDates(List<ConcertDate> concertDates) {

        return concertDates.stream()
                .map(concertDate -> {
                    boolean available = concertRepository.existSeatByConcertDateAndStatus(concertDate.getConcertDateId(), SeatStatus.AVAILABLE);
                    return ConcertDateInfo.of(concertDate, available);
                })
                .toList();
    }

    List<ConcertSeatInfo> readSeats(List<Seat> seats) {
        return seats.stream()
                .map(ConcertSeatInfo::of)
                .toList();
    }

    public List<ReservationInfo> readReservations(List<Reservation> reservations) {
        return reservations.stream()
                .map(reservation -> {
                    Payment payment = paymentRepository.findByReservationId(reservation.getReservationId())
                            .orElseThrow(() -> new CustomException(PAYMENT_IS_NOT_FOUND,
                                    "해당 예약에 대한 결제 내역을 조회할 수 없습니다. [reservationId: %d]"
                                            .formatted(reservation.getReservationId())));

                    return ReservationInfo.of(reservation,
                            ReservationConcertInfo.of(reservation),
                            ReservationPaymentInfo.of(payment));
                })
                .toList();
    }

    public ReservationInfo readReservation(Reservation reservation,
                                           ConcertDate concertDate, Seat seat,
                                           Payment payment) {
        return ReservationInfo.of(reservation,
                ReservationConcertInfo.of(concertDate, seat),
                ReservationPaymentInfo.of(payment));

    }
}
