package com.hhplus.hhplusconcert.domain.concert.command;

public class ReservationCommand {
    public record Create(
            Long concertId,
            Long concertDateId,
            int seatNumber,
            Long userId
    ) {
    }
}
