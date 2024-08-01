package com.hhplus.hhplusconcert.interfaces.controller.queue;

import com.hhplus.hhplusconcert.application.queue.WaitingQueueFacade;
import com.hhplus.hhplusconcert.interfaces.controller.common.dto.ApiResultResponse;
import com.hhplus.hhplusconcert.interfaces.controller.queue.dto.WaitingQueueDto;
import com.hhplus.hhplusconcert.support.aop.TraceLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "대기열", description = "WaitingQueue-controller")
@TraceLog
@RequiredArgsConstructor
@RequestMapping("/v1/queues")
public class WaitingQueueController {

    private final WaitingQueueFacade waitingQueueFacade;

    /**
     * 대기열 활성여부 조회
     *
     * @param request userId 정보
     * @return ApiResultResponse 토큰 정보와 대기열 정보를 반환한다.
     */
    @Operation(summary = "대기열 활성여부 조회", description = "토큰 요청값이 없으면 새로 발급하여 응답 반환, isActive 반환값에 따라 페이지 진입 가능 여부를 판단합니다.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = WaitingQueueDto.Response.class)))
    @PostMapping("/token")
    public ApiResultResponse<WaitingQueueDto.Response> checkWaiting(@RequestBody @Valid WaitingQueueDto.Request request) {
        return ApiResultResponse.ok(
                WaitingQueueDto.Response.of(waitingQueueFacade.checkWaiting(request.toCreateCommand())));
    }
}
