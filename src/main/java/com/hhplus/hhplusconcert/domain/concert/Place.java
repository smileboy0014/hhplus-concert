package com.hhplus.hhplusconcert.domain.concert;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class Place {

    private Long placeId;

    private String name;

    private int totalSeat;

}