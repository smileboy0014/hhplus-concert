package com.hhplus.hhplusconcert.interfaces.controller.concert;

import com.hhplus.hhplusconcert.application.concert.ConcertFacade;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ConcertDateResponse;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ConcertResponse;
import com.hhplus.hhplusconcert.domain.concert.service.dto.ConcertSeatResponse;
import com.hhplus.hhplusconcert.interfaces.controller.common.dto.ApiResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "콘서트", description = "Concert-controller")
@RequiredArgsConstructor
@RequestMapping("/v1/concerts")
public class ConcertController {

    private final ConcertFacade concertFacade;

    /**
     * 콘서트 목록 조회
     *
     * @return ApiResultResponse 콘서트 목록을 반환한다.
     */
    @Operation(summary = "콘서트 목록 조회")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ConcertResponse.class))))
    @GetMapping
    public ApiResultResponse<List<ConcertResponse>> getConcerts() {

        return ApiResultResponse.ok(concertFacade.getConcerts());
    }

    /**
     * 콘서트 상세 조회
     *
     * @param concertId 정보
     * @return ApiResultResponse 콘서트 상세 정보를 반환한다.
     */
    @Operation(summary = "콘서트 상세 조회")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ConcertResponse.class)))
    @GetMapping("/{concertId}")
    public ApiResultResponse<ConcertResponse> getConcert(@PathVariable(name = "concertId") @NotNull Long concertId) {

        return ApiResultResponse.ok(concertFacade.getConcert(concertId));
    }

    /**
     * 콘서트 예약 가능한 날짜 조회
     *
     * @param concertId concertId 정보
     * @return ApiResultResponse 콘서트 예약 가능한 날짜를 반환한다.
     */
    @Operation(summary = "콘서트 예약 가능한 날짜 조회")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ConcertDateResponse.class))))
    @GetMapping("/{concertId}/dates")
    public ApiResultResponse<List<ConcertDateResponse>> getConcertDates(@PathVariable(name = "concertId") @NotNull Long concertId) {

        return ApiResultResponse.ok(concertFacade.getConcertDates(concertId));
    }

    /**
     * 콘서트 예약 가능한 좌석 조회
     *
     * @param concertDateId concertDateId 정보
     * @return ApiResultResponse 콘서트 예약 가능한 좌석 정보를 반환한다.
     */
    @Operation(summary = "콘서트 예약 가능한 좌석 조회")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ConcertSeatResponse.class))))
    @GetMapping("/dates/{concertDateId}/seats")
    public ApiResultResponse<List<ConcertSeatResponse>> getAvailableSeats(@PathVariable(name = "concertDateId") @NotNull Long concertDateId) {
        return ApiResultResponse.ok(concertFacade.getAvailableSeats(concertDateId));

    }
}
