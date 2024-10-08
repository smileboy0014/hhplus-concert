package com.hhplus.hhplusconcert.domain.concert;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class Concert {

    private Long concertId; // 콘서트 ID

    private String name; // 콘서트 이름

    List<ConcertDate> concertDates;

}