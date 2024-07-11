package com.hhplus.hhplusconcert.domain.concert.service.dto;

import com.hhplus.hhplusconcert.domain.concert.entity.Seat;
import lombok.Builder;

@Builder
public record ConcertSeatResponse(Long seatId, int seatNumber) {

    public static ConcertSeatResponse of(Seat seat) {
        return ConcertSeatResponse.builder()
                .seatId(seat.getSeatId())
                .seatNumber(seat.getSeatNumber())
                .build();

    }
}
