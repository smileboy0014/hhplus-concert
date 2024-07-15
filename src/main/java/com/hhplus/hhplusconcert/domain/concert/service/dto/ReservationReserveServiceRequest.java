package com.hhplus.hhplusconcert.domain.concert.service.dto;

import com.hhplus.hhplusconcert.domain.concert.entity.ConcertDate;
import com.hhplus.hhplusconcert.domain.concert.entity.Reservation;
import com.hhplus.hhplusconcert.domain.concert.entity.Seat;
import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import com.hhplus.hhplusconcert.domain.payment.enums.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.hhplus.hhplusconcert.domain.concert.enums.ReservationStatus.TEMPORARY_RESERVED;

@Builder
public record ReservationReserveServiceRequest(Long concertId,
                                               Long concertDateId,
                                               int seatNumber,
                                               Long userId) {
    public Reservation toReservationEntity(ConcertDate concertDate, Seat seat) {

        return Reservation.builder()
                .concertId(concertDate.getConcertInfo().getConcertId())
                .userId(userId)
                .seatId(seat.getSeatId())
                .concertDateId(concertDateId)
                .concertName(concertDate.getConcertInfo().getName())
                .concertDate(concertDate.getConcertDate())
                .seatNumber(seat.getSeatNumber())
                .reservedAt(LocalDateTime.now())
                .status(TEMPORARY_RESERVED)
                .build();
    }

    public Payment toPaymentEntity(Reservation reservation, Seat seat) {

        return Payment.builder()
                .price(seat.getPrice())
                .paymentPrice(BigDecimal.ZERO)
                .status(PaymentStatus.WAIT)
                .paidAt(null)
                .reservation(reservation)
                .build();
    }
}
