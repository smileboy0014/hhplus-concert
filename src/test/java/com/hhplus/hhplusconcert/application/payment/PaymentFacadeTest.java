package com.hhplus.hhplusconcert.application.payment;

import com.hhplus.hhplusconcert.domain.payment.enums.PaymentStatus;
import com.hhplus.hhplusconcert.domain.payment.service.PaymentService;
import com.hhplus.hhplusconcert.domain.payment.service.dto.PayServiceRequest;
import com.hhplus.hhplusconcert.domain.payment.service.dto.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class PaymentFacadeTest {

    @Mock
    PaymentService paymentService;

    @InjectMocks
    PaymentFacade paymentFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("결제 요청을 하는 유즈케이스를 실행한다.")
    void pay() {
        // given
        PayServiceRequest request = PayServiceRequest
                .builder()
                .userId(1L)
                .reservationId(1L)
                .build();

        PaymentResponse response = PaymentResponse
                .builder()
                .paymentId(1L)
                .status(PaymentStatus.COMPLETE.getStatus())
                .build();

        when(paymentService.pay(request)).thenReturn(response);

        // when
        PaymentResponse result = paymentFacade.pay(request);

        // then
        assertThat(result.status()).isEqualTo(PaymentStatus.COMPLETE.getStatus());
    }

}