package com.hhplus.hhplusconcert.application.payment;

import com.hhplus.hhplusconcert.domain.payment.service.PaymentService;
import com.hhplus.hhplusconcert.domain.payment.service.dto.PayServiceRequest;
import com.hhplus.hhplusconcert.domain.payment.service.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;

    /**
     * 결제 요청하는 유즈케이스를 실행한다.
     *
     * @param request reservationId, userId 정보
     * @return PaymentResponse 결제 결과를 반환한다.
     */
    public PaymentResponse pay(PayServiceRequest request) {
        return paymentService.pay(request);
    }
}
