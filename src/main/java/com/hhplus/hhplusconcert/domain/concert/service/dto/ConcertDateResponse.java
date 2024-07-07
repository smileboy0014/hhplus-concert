package com.hhplus.hhplusconcert.domain.concert.service.dto;

import lombok.Builder;

@Builder
public record ConcertDateResponse(Long concertDateId, String place,
                                  String concertDate, boolean isSoldOut
) {
}
