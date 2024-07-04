package com.hhplus.hhplusconcert.domain.reservation.service.dto;

import lombok.Builder;

@Builder
public record ReservationConcertResponse(Long concertId, String name,
                                         Long concertDateId, Long seatId,
                                         int seatNumber) {
}
