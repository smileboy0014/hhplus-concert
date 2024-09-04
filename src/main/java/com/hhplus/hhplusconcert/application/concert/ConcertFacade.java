package com.hhplus.hhplusconcert.application.concert;

import com.hhplus.hhplusconcert.domain.concert.Concert;
import com.hhplus.hhplusconcert.domain.concert.ConcertDate;
import com.hhplus.hhplusconcert.domain.concert.ConcertService;
import com.hhplus.hhplusconcert.domain.concert.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<Concert> getConcerts(Pageable pageable) {
        return concertService.getConcerts(pageable);
    }

    /**
     * 콘서트 상세 정보를 요청하는 유즈케이스를 실행한다.
     *
     * @param concertId concertId 정보
     * @return ConcertResponse 콘서트 상세 정보를 반환한다.
     */
    public Concert getConcert(Long concertId) {
        return concertService.getConcert(concertId);
    }

    /**
     * 콘서트 예약 가능한 날짜를 요청하는 유즈케이스를 실행한다.
     *
     * @param concertId concertId 정보
     * @return ConcertDateResponse 콘서트 예약 가능한 날짜 정보를 반환한다.
     */
    public List<ConcertDate> getAvailableConcertDates(Long concertId) {
        return concertService.getAvailableConcertDates(concertId);
    }

    /**
     * 예약 가능한 좌석을 요청하는 유즈케이스를 실행한다.
     *
     * @param concertDateId concertDateId 정보
     * @return ConcertSeatResponse 예약 가능한 좌석 정보를 반환한다.
     */
    public List<Seat> getAvailableSeats(Long concertDateId) {
        return concertService.getAvailableSeats(concertDateId);
    }

}
