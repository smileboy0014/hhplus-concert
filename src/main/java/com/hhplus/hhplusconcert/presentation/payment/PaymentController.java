package com.hhplus.hhplusconcert.presentation.payment;

import com.hhplus.hhplusconcert.domain.payment.service.dto.PaymentResponse;
import com.hhplus.hhplusconcert.presentation.common.dto.ApiResponse;
import com.hhplus.hhplusconcert.presentation.payment.dto.PayRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/v1/payment")
public class PaymentController {

    @PostMapping("/{paymentId}")
    public ApiResponse<PaymentResponse> pay(@PathVariable @NotNull Long paymentId,
                                            @RequestBody @Valid PayRequest request) {
        return ApiResponse.ok(new PaymentResponse(paymentId, "결제 완료",
                BigDecimal.valueOf(90000), BigDecimal.valueOf(13000)));
    }

}
