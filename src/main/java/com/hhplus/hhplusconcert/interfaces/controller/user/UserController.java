package com.hhplus.hhplusconcert.interfaces.controller.user;

import com.hhplus.hhplusconcert.application.user.UserFacade;
import com.hhplus.hhplusconcert.interfaces.controller.common.dto.ApiResultResponse;
import com.hhplus.hhplusconcert.interfaces.controller.user.dto.UserBalanceDto;
import com.hhplus.hhplusconcert.support.aop.TraceLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@Tag(name = "유저 관련(잔액)", description = "Reservation-controller")
@TraceLog
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

    private final UserFacade userFacade;

    /**
     * 잔액 조회
     *
     * @param userId userId 정보
     * @return ApiResultResponse 유저의 잔액 정보를 반환한다.
     */
    @Operation(summary = "잔액 조회")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UserBalanceDto.Response.class)))
    @GetMapping("/{userId}/balance")
    public ApiResultResponse<UserBalanceDto.Response> getBalance(@PathVariable("userId") @NotNull(message = "유저 ID는 필수값 입니다.") Long userId) {

        return ApiResultResponse.ok(
                UserBalanceDto.Response.of(userFacade.getBalance(userId)));
    }

    /**
     * 잔액 충전
     *
     * @param userId  userId 정보
     * @param request 충전하려는 amount 정보
     * @return ApiResultResponse 유저의 충전된 잔액 정보를 반환한다.
     */
    @Operation(summary = "잔액 충전")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UserBalanceDto.Response.class)))
    @PatchMapping("/{userId}/charge")
    public ApiResultResponse<UserBalanceDto.Response> chargeBalance(@PathVariable("userId")
                                                                    @NotNull(message = "유저 ID는 필수값 입니다.") Long userId,
                                                                    @RequestBody UserBalanceDto.Request request) {
        return ApiResultResponse.ok(
                UserBalanceDto.Response.of(userFacade.chargeBalance(request.toCreateCommand(userId))));

    }
}
