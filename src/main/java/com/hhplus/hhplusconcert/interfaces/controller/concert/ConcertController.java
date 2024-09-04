package com.hhplus.hhplusconcert.interfaces.controller.concert;

import com.hhplus.hhplusconcert.application.concert.ConcertFacade;
import com.hhplus.hhplusconcert.interfaces.controller.common.dto.ApiResultResponse;
import com.hhplus.hhplusconcert.interfaces.controller.concert.dto.ConcertDateDto;
import com.hhplus.hhplusconcert.interfaces.controller.concert.dto.ConcertDto;
import com.hhplus.hhplusconcert.interfaces.controller.concert.dto.ConcertSeatDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "콘서트", description = "Concert-controller")
@TraceLog
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
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ConcertDto.Response.class))))
    @GetMapping
    public ApiResultResponse<Page<ConcertDto.Response>> getConcerts(Pageable pageable) {

        return ApiResultResponse.ok(
                concertFacade.getConcerts(pageable)
                        .map(ConcertDto.Response::of));
    }

    /**
     * 콘서트 등록
     *
     * @return ApiResultResponse 콘서트를 등록합니다.
     */
    @Operation(summary = "콘서트 등록")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ConcertDto.Response.class))))
    @PostMapping
    public ApiResultResponse<ConcertDto.Response> saveConcert(@RequestBody @Valid ConcertDto.Request request) {

        return ApiResultResponse.ok(
                ConcertDto.Response.of(concertFacade.saveConcert(request.toCreateCommand())));
    }

    /**
     * 콘서트 상세 조회
     *
     * @param concertId 정보
     * @return ApiResultResponse 콘서트 상세 정보를 반환한다.
     */
    @Operation(summary = "콘서트 상세 조회")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ConcertDto.Response.class)))
    @GetMapping("/{concertId}")
    public ApiResultResponse<ConcertDto.Response> getConcert(@PathVariable(name = "concertId") @NotNull Long concertId) {

        return ApiResultResponse.ok(ConcertDto.Response.of(concertFacade.getConcert(concertId)));
    }

    /**
     * 콘서트 예약 가능한 날짜 조회
     *
     * @param concertId concertId 정보
     * @return ApiResultResponse 콘서트 예약 가능한 날짜를 반환한다.
     */
    @Operation(summary = "콘서트 예약 가능한 날짜 조회")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ConcertDateDto.Response.class))))
    @GetMapping("/{concertId}/dates")
    public ApiResultResponse<List<ConcertDateDto.Response>> getAvailableConcertDates(@PathVariable(name = "concertId") @NotNull Long concertId) {

        return ApiResultResponse.ok(
                concertFacade.getAvailableConcertDates(concertId).stream()
                        .map(ConcertDateDto.Response::of)
                        .toList());
    }


    /**
     * 콘서트 예약 가능한 좌석 조회
     *
     * @param concertDateId concertDateId 정보
     * @return ApiResultResponse 콘서트 예약 가능한 좌석 정보를 반환한다.
     */
    @Operation(summary = "콘서트 예약 가능한 좌석 조회")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ConcertSeatDto.Response.class))))
    @GetMapping("/dates/{concertDateId}/seats")
    public ApiResultResponse<List<ConcertSeatDto.Response>> getAvailableSeats(@PathVariable(name = "concertDateId") @NotNull Long concertDateId) {
        return ApiResultResponse.ok(
                concertFacade.getAvailableSeats(concertDateId).stream()
                        .map(ConcertSeatDto.Response::of)
                        .toList());

    }
}
