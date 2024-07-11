package com.hhplus.hhplusconcert.interfaces.controller.queue;

import com.hhplus.hhplusconcert.application.queue.WaitingQueueFacade;
import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueResponse;
import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueTokenResponse;
import com.hhplus.hhplusconcert.interfaces.controller.common.dto.ApiResultResponse;
import com.hhplus.hhplusconcert.interfaces.controller.queue.dto.WaitingQueueEnterRequest;
import com.hhplus.hhplusconcert.interfaces.controller.queue.dto.WaitingQueueTokenRequest;
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
@RequiredArgsConstructor
@RequestMapping("/v1/queues")
public class WaitingQueueController {

    private final WaitingQueueFacade waitingQueueFacade;

    /**
     * 토큰 발급 요청
     *
     * @param request userId 정보
     * @return ApiResultResponse 토큰 정보를 반환한다.
     */
    @Operation(summary = "토큰 발급 요청")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = WaitingQueueTokenResponse.class)))
    @PostMapping("/issue-token")
    public ApiResultResponse<WaitingQueueTokenResponse> issueToken(@RequestBody @Valid WaitingQueueTokenRequest request) {
        return ApiResultResponse.ok(waitingQueueFacade.issueToken(request.toServiceRequest()));
    }

    /**
     * 대기열 저장
     *
     * @param request userId, token 정보
     * @return ApiResultResponse 대기열 정보를 반환한다.
     */
    @Operation(summary = "대기열 저장")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = WaitingQueueResponse.class)))
    @PostMapping("/enter")
    public ApiResultResponse<WaitingQueueResponse> enterQueue(@RequestBody @Valid WaitingQueueEnterRequest request) {
        return ApiResultResponse.ok(waitingQueueFacade.enterQueue(request.toServiceRequest()));
    }

    /**
     * 대기열 확인
     *
     * @param request userId, token 정보
     * @return ApiResultResponse 대기열 정보를 반환한다.
     */
    @Operation(summary = "대기열 확인")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = WaitingQueueResponse.class)))
    @PostMapping("/check")
    public ApiResultResponse<WaitingQueueResponse> checkQueue(@RequestBody @Valid WaitingQueueEnterRequest request) {

        return ApiResultResponse.ok(waitingQueueFacade.checkQueue(request.toServiceRequest()));

    }


}
