package com.hhplus.hhplusconcert.domain.concert.service.dto;

import com.hhplus.hhplusconcert.domain.concert.entity.ConcertDate;
import lombok.Builder;

@Builder
public record ConcertDateInfo(Long concertDateId, String place,
                              String concertDate, boolean isAvailable
) {

    public static ConcertDateInfo of(ConcertDate concertDate, boolean available) {
        return ConcertDateInfo.builder()
                .concertDateId(concertDate.getConcertDateId())
                .concertDate(concertDate.getConcertDate())
                .place(concertDate.getPlaceInfo().getName())
                .isAvailable(available)
                .build();
    }
}
