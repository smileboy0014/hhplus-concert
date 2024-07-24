package com.hhplus.hhplusconcert.interfaces.controller.concert.dto;

import com.hhplus.hhplusconcert.domain.concert.Seat;
import lombok.Builder;

import java.math.BigDecimal;

public class ConcertSeatDto {

    @Builder(toBuilder = true)
    public record Response(Long seatId, int seatNumber, Seat.TicketClass ticketClass, BigDecimal price) {

        public static ConcertSeatDto.Response of(Seat seat) {
            return Response.builder()
                    .seatId(seat.getSeatId())
                    .seatNumber(seat.getSeatNumber())
                    .ticketClass(seat.getTicketClass())
                    .price(seat.getPrice())
                    .build();

        }
    }
}
