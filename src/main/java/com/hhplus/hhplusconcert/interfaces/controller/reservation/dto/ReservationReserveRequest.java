package com.hhplus.hhplusconcert.interfaces.controller.reservation.dto;

import com.hhplus.hhplusconcert.domain.concert.service.dto.ReservationReserveServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;


@Builder
public record ReservationReserveRequest(@NotNull Long concertId,
                                        @NotNull Long concertDateId,
                                        @NotNull int seatNumber,
                                        @NotNull Long userId) {
    public ReservationReserveServiceRequest toServiceRequest() {
        return ReservationReserveServiceRequest.builder()
                .concertId(concertId)
                .concertDateId(concertDateId)
                .seatNumber(seatNumber)
                .userId(userId)
                .build();
    }
}
