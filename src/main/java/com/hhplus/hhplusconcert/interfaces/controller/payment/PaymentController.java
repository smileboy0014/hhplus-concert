package com.hhplus.hhplusconcert.interfaces.controller.payment;

import com.hhplus.hhplusconcert.application.payment.PaymentFacade;
import com.hhplus.hhplusconcert.interfaces.controller.common.dto.ApiResultResponse;
import com.hhplus.hhplusconcert.interfaces.controller.payment.dto.PaymentDto;
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
@Tag(name = "결제", description = "Payment-controller")
@TraceLog
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
public class PaymentController {

    private final PaymentFacade paymentFacade;

    /**
     * 결제 요청
     *
     * @param request reservationId, userId 정보
     * @return ApiResultResponse 결제 결과를 반환한다.
     */
    @Operation(summary = "결제 요청")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = PaymentDto.Response.class)))
    @PostMapping("/pay")
    public ApiResultResponse<PaymentDto.Response> pay(@RequestBody @Valid PaymentDto.Request request) {

        return ApiResultResponse.ok(PaymentDto.Response.of(paymentFacade.pay(request.toCreateCommand())));
    }

}