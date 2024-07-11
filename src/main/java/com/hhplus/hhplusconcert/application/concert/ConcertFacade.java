package com.hhplus.hhplusconcert.application.concert;

import com.hhplus.hhplusconcert.domain.concert.service.ConcertService;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ConcertDateResponse;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ConcertResponse;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ConcertSeatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConcertFacade {

    private final ConcertService concertService;

    /**
     * 콘서트 목록을 요청하는 유즈케이스를 실행한다.
     *
     * @return ConcertResponse 콘서트 목록을 반환한다.
     */
    public List<ConcertResponse> getConcerts() {
        return concertService.getConcerts();
    }

    /**
     * 콘서트 상세 정보를 요청하는 유즈케이스를 실행한다.
     *
     * @param concertId concertId 정보
     * @return ConcertResponse 콘서트 상세 정보를 반환한다.
     */
    public ConcertResponse getConcert(Long concertId) {
        return concertService.getConcert(concertId);
    }

    /**
     * 콘서트 예약 가능한 날짜를 요청하는 유즈케이스를 실행한다.
     *
     * @param concertId concertId 정보
     * @return ConcertDateResponse 콘서트 예약 가능한 날짜 정보를 반환한다.
     */
    public List<ConcertDateResponse> getConcertDates(Long concertId) {
        return concertService.getConcertDates(concertId);
    }

    /**
     * 예약 가능한 좌석을 요청하는 유즈케이스를 실행한다.
     *
     * @param concertDateId concertDateId 정보
     * @return ConcertSeatResponse 예약 가능한 좌석 정보를 반환한다.
     */
    public List<ConcertSeatResponse> getAvailableSeats(Long concertDateId) {

        return concertService.getAvailableSeats(concertDateId);
    }
}
