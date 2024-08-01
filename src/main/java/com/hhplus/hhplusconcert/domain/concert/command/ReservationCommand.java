package com.hhplus.hhplusconcert.domain.concert.command;

import com.hhplus.hhplusconcert.domain.concert.ConcertDate;
import com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo;
import com.hhplus.hhplusconcert.domain.concert.Seat;

public class ReservationCommand {
    public record Create(
            Long concertId,
            Long concertDateId,
            int seatNumber,
            Long userId
    ) {
        public ConcertReservationInfo toReservationDomain(Seat seat, ConcertDate concertDate) {

            return ConcertReservationInfo.builder()
                    .concertId(concertId())
                    .concertDateId(concertDateId())
                    .userId(userId())
                    .seatId(seat.getSeatId())
                    .concertName(concertDate.getConcert().getName())
                    .concertDate(concertDate.getConcertDate())
                    .seatNumber(seat.getSeatNumber())
                    .seatPrice(seat.getPrice())
                    .build();
        }
    }
}
