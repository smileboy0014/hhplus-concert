package com.hhplus.hhplusconcert.interfaces.controller.reservation.dto;

import com.hhplus.hhplusconcert.domain.concert.command.CancelReservationCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class CancelReservationDto {

    @Builder(toBuilder = true)
    public record Request(@NotNull Long userId) {

        public CancelReservationCommand.Delete toDeleteCommand(Long reservationId){
            return new CancelReservationCommand.Delete(reservationId, userId);
        }


    }

}
