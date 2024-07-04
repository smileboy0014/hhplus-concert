package com.hhplus.hhplusconcert.domain.concert.service.dto;

import lombok.Builder;

@Builder
public record ConcertSeatResponse(Long seatId, int seatNumber,
                                  String place) {

}
