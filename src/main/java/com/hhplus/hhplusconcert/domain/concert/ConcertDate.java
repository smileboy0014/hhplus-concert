package com.hhplus.hhplusconcert.domain.concert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class ConcertDate {

    private Long concertDateId;

    private Concert concert;

    private Place place;

    private Long concertId;

    private Long placeId;

    private String concertDate;

    private Boolean isAvailable;

}
