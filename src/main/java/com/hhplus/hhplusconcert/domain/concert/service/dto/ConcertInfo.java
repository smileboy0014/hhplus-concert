package com.hhplus.hhplusconcert.domain.concert.service.dto;

import com.hhplus.hhplusconcert.domain.concert.entity.Concert;
import com.hhplus.hhplusconcert.domain.concert.entity.ConcertDate;
import lombok.Builder;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.hhplus.hhplusconcert.support.utils.StringUtils.getPeriod;

@Builder
public record ConcertInfo(Long concertId, String name,
                          String place,
                          String period
) {

    public static ConcertInfo of(Concert concert, List<ConcertDate> concertDates) {
        return ConcertInfo.builder()
                .concertId(concert.getConcertId())
                .name(concert.getName())
                .place(concertDates.isEmpty() ? "-" : concertDates.get(0).getPlaceInfo().getName())
                .period(getConcertPeriod(concertDates))
                .build();
    }

    public static String getConcertPeriod(List<ConcertDate> concertDates) {

        return Optional.ofNullable(concertDates)
                .filter(dates -> !dates.isEmpty())
                .map(dates -> {
                    if (dates.size() == 1) {
                        return dates.get(0).getConcertDate();
                    }
                    List<ConcertDate> sortedDates = dates.stream()
                            .sorted(Comparator.comparing(ConcertDate::getConcertDate))
                            .toList();

                    return getPeriod(sortedDates.get(0).getConcertDate(),
                            sortedDates.get(sortedDates.size() - 1).getConcertDate());
                })
                .orElse("-");

    }
}
