package com.hhplus.hhplusconcert.presentation.reservation;

import com.hhplus.hhplusconcert.domain.reservation.service.dto.ReservationConcertResponse;
import com.hhplus.hhplusconcert.domain.reservation.service.dto.ReservationPaymentResponse;
import com.hhplus.hhplusconcert.domain.reservation.service.dto.ReservationResponse;
import com.hhplus.hhplusconcert.presentation.common.dto.ApiResponse;
import com.hhplus.hhplusconcert.presentation.reservation.dto.ReserveRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/v1/reservation")
public class ReservationController {

    // 좌석 예약 요청
    @PostMapping
    public ApiResponse<ReservationResponse> reserveSeat(@RequestBody @Valid ReserveRequest request) {
        return ApiResponse.ok(new ReservationResponse(1L, "진행 중",
                new ReservationConcertResponse(2L, "싸이 흠뻑쇼", request.concertDateId(), request.seatId(), 29),
                new ReservationPaymentResponse(4L, "결제 대기", BigDecimal.ZERO)));
    }

    // 예약 내역 조회
    @GetMapping("/{userId}")
    public ApiResponse<List<ReservationResponse>> getReservation(@PathVariable @NotNull Long userId) {
        return ApiResponse.ok(List.of(new ReservationResponse(1L, "예약 완료",
                        new ReservationConcertResponse(2L, "싸이 흠뻑쇼", 5L, 10L, 29),
                        new ReservationPaymentResponse(4L, "결제 완료", BigDecimal.valueOf(100000))),
                new ReservationResponse(2L, "예약 취소",
                        new ReservationConcertResponse(5L, "god 콘서트", 10L, 12L, 49),
                        new ReservationPaymentResponse(2L, "결제 취소", BigDecimal.valueOf(78000))
                )));
    }

    // 예약 취소
    @DeleteMapping("/{reservationId}")
    public ApiResponse<Long> cancelSeat(@PathVariable @NotNull Long reservationId) {
        return ApiResponse.ok(reservationId);
    }

}
