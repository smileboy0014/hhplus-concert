package com.hhplus.hhplusconcert.domain.concert.command;

public class CancelReservationCommand {

    public record Delete(
            Long reservationId,
            Long userId

    ) {
    }

}
