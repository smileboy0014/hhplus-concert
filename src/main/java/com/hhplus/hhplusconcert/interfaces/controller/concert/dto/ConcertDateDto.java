package com.hhplus.hhplusconcert.interfaces.controller.concert.dto;

import com.hhplus.hhplusconcert.domain.concert.ConcertDate;
import lombok.Builder;

public class ConcertDateDto {

    @Builder(toBuilder = true)
    public record Response(Long concertDateId, String place,
                           String concertDate, boolean isAvailable) {
        public static ConcertDateDto.Response of(ConcertDate concertDate) {
            return Response.builder()
                    .concertDateId(concertDate.getConcertDateId())
                    .place(concertDate.getPlace().getName())
                    .concertDate(concertDate.getConcertDate())
                    .isAvailable(concertDate.getIsAvailable())
                    .build();

        }
    }
}
