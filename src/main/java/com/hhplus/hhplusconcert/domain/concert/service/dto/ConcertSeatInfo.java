package com.hhplus.hhplusconcert.domain.concert.service.dto;

import com.hhplus.hhplusconcert.domain.concert.entity.Seat;
import com.hhplus.hhplusconcert.domain.concert.enums.TicketClass;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ConcertSeatInfo(Long seatId, int seatNumber, TicketClass ticketClass, BigDecimal price) {

    public static ConcertSeatInfo of(Seat seat) {
        return ConcertSeatInfo.builder()
                .seatId(seat.getSeatId())
                .seatNumber(seat.getSeatNumber())
                .ticketClass(seat.getTicketClass())
                .price(seat.getPrice())
                .build();

    }
}
