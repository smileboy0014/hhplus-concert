package com.hhplus.hhplusconcert.interfaces.controller.concert.dto;

import com.hhplus.hhplusconcert.domain.concert.Concert;
import com.hhplus.hhplusconcert.domain.concert.ConcertDate;
import lombok.Builder;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.hhplus.hhplusconcert.support.utils.StringUtils.getPeriod;

public class ConcertDto {

    @Builder(toBuilder = true)
    public record Response(Long concertId, String name, String place, String period) {
        public static Response of(Concert concert) {
            return Response.builder()
                    .concertId(concert.getConcertId())
                    .name(concert.getName())
                    .place(!concert.getConcertDates().isEmpty() ? concert.getConcertDates().get(0).getPlace().getName()
                            : "-")
                    .period(getConcertPeriod(concert.getConcertDates()))
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
}
