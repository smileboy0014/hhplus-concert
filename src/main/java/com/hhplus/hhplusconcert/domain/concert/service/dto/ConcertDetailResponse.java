package com.hhplus.hhplusconcert.domain.concert.service.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ConcertDetailResponse(Long concertId, String name,
                                    String place,
                                    LocalDateTime createdAt,
                                    String period
) {
}
