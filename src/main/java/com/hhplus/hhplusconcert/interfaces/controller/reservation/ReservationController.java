package com.hhplus.hhplusconcert.interfaces.controller.reservation;

import com.hhplus.hhplusconcert.application.reservation.ReservationFacade;
import com.hhplus.hhplusconcert.interfaces.controller.common.dto.ApiResultResponse;
import com.hhplus.hhplusconcert.interfaces.controller.reservation.dto.CancelReservationDto;
import com.hhplus.hhplusconcert.interfaces.controller.reservation.dto.ReservationDto;
import com.hhplus.hhplusconcert.support.aop.TraceLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Tag(name = "예약", description = "Reservation-controller")
@TraceLog
@RequiredArgsConstructor
@RequestMapping("/v1/reservations")
public class ReservationController {

    public static final String SUCCESS_CANCEL_RESERVATION = "예약을 취소하는데 성공하였습니다";

    private final ReservationFacade reservationFacade;

    /**
     * 좌석 예약 요청
     *
     * @param request concertId, concertDateId, seatNumber, userId 정보
     * @return ApiResultResponse 예약 완료 정보를 반환한다.
     */
    @Operation(summary = "좌석 예약 요청")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ReservationDto.Response.class)))
    @PostMapping
    public ApiResultResponse<ReservationDto.Response> reserveSeat(@RequestBody @Valid ReservationDto.Request request) {

        return ApiResultResponse.ok(ReservationDto.Response.of(reservationFacade.reserveSeat(request.toCreateCommand())));
    }

    /**
     * 나의 예약 내역 조회
     *
     * @param userId userId 정보
     * @return ApiResultResponse 나의 예약 내역을 반환한다.
     */
    @Operation(summary = "예약 내역 조회")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservationDto.Response.class))))
    @GetMapping("/{userId}")
    public ApiResultResponse<List<ReservationDto.Response>> getMyReservations(@PathVariable(name = "userId") @NotNull Long userId) {

        return ApiResultResponse.ok(
                reservationFacade.getMyReservations(userId).stream()
                        .map(ReservationDto.Response::of).toList());
    }

    /**
     * 예약 취소
     *
     * @param reservationId reservationId 정보
     * @return ApiResultResponse 예약 취소 성공 메세지를 반환합니다.
     */
    @Operation(summary = "예약 취소")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = String.class)))
    @DeleteMapping("/{reservationId}")
    public ApiResultResponse<String> cancelReservation(@PathVariable(name = "reservationId") @NotNull Long reservationId,
                                                        @Valid CancelReservationDto.Request request) {
        reservationFacade.cancelReservation(request.toDeleteCommand(reservationId));

        return ApiResultResponse.ok(SUCCESS_CANCEL_RESERVATION);
    }

}
