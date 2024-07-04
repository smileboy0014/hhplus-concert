package com.hhplus.hhplusconcert.presentation.concert;

import com.hhplus.hhplusconcert.domain.concert.service.dto.ConcertDateResponse;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ConcertDetailResponse;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ConcertResponse;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ConcertSeatResponse;
import com.hhplus.hhplusconcert.presentation.common.dto.ApiResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static com.hhplus.hhplusconcert.application.common.date.DateUtil.getLocalDateTimeToString;

@RestController
@RequestMapping("/v1/concerts")
public class ConcertController {

    // 콘서트 목록 조회
    @GetMapping
    public ApiResponse<List<ConcertResponse>> getConcerts() {
        return ApiResponse.ok(List.of(
                new ConcertResponse(1L, "싸이 흠뻑쇼", "서울대공원", LocalDateTime.now().minusMonths(2)),
                new ConcertResponse(2L, "god 콘서트", "올림픽공원", LocalDateTime.now().minusMonths(1))));

    }

    // 콘서트 상세 조회
    @GetMapping("/{concertId}")
    public ApiResponse<ConcertDetailResponse> getConcert(@PathVariable @NotNull Long concertId) {
        return ApiResponse.ok(new ConcertDetailResponse(concertId, "god 콘서트", "올림픽공원",
                LocalDateTime.now().minusMonths(1),
                "2024-06-27~2024-06-28"));
    }

    // 콘서트 예약 가능한 날짜 조회
    @GetMapping("/{concertId}/dates")
    public ApiResponse<List<ConcertDateResponse>> getConcertDates(@PathVariable @NotNull Long concertId) {
        return ApiResponse.ok(List.of(new ConcertDateResponse(3L, "서울대공원",
                        getLocalDateTimeToString(LocalDateTime.now().minusMonths(2)), false),
                (new ConcertDateResponse(4L, "서울대공원",
                        getLocalDateTimeToString(LocalDateTime.now().minusMonths(2).plusDays(1)), true)
                )));
    }

    // 콘서트 좌석 조회
    @GetMapping("/{concertId}/dates/{concertDateId}/seats")
    public ApiResponse<List<ConcertSeatResponse>> getSeats(@PathVariable @NotNull Long concertId,
                                                           @PathVariable @NotNull Long concertDateId) {
        return ApiResponse.ok(List.of(new ConcertSeatResponse(3L, 10, "서울대공원"),
                new ConcertSeatResponse(5L, 15, "서울대공원")));
    }


}
