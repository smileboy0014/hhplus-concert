package com.hhplus.hhplusconcert.domain.concert.service.dto;

import com.hhplus.hhplusconcert.domain.concert.entity.ConcertDate;
import com.hhplus.hhplusconcert.domain.concert.entity.Reservation;
import com.hhplus.hhplusconcert.domain.concert.entity.Seat;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ReservationConcertResponse(Long concertId,
                                         Long concertDateId,
                                         Long seatId,
                                         String concertName,
                                         String concertDate,
                                         BigDecimal seatPrice,
                                         int seatNumber) {

    public static ReservationConcertResponse of(ConcertDate concertDate, Seat seat) {

        return ReservationConcertResponse.builder()
                .concertId(concertDate.getConcertInfo().getConcertId())
                .concertDateId(concertDate.getConcertDateId())
                .seatId(seat.getSeatId())
                .concertName(concertDate.getConcertInfo().getName())
                .concertDate(concertDate.getConcertDate())
                .seatNumber(seat.getSeatNumber())
                .seatPrice(seat.getPrice())
                .build();
    }

    public static ReservationConcertResponse of(Reservation reservation) {

        return ReservationConcertResponse.builder()
                .concertId(reservation.getConcertId())
                .concertDateId(reservation.getConcertDateId())
                .seatId(reservation.getSeatId())
                .concertName(reservation.getConcertName())
                .concertDate(reservation.getConcertDate())
                .seatNumber(reservation.getSeatNumber())
                .build();
    }
}
