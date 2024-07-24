package com.hhplus.hhplusconcert.domain.payment;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.hhplus.hhplusconcert.domain.payment.Payment.PaymentStatus;
import static com.hhplus.hhplusconcert.domain.payment.Payment.builder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentTest {

    @Test
    @DisplayName("결제를 완료한다.")
    void completePayment() {
        //given
        Payment payment = builder()
                .build();
        //when
        payment.complete();

        //then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETE);
    }

    @Test
    @DisplayName("이미 결제가 되었으면 PAYMENT_ALREADY_COMPLETE 예외를 반환한다.")
    void completePaymentWhenAlreadyPaymentComplete() {
        //given
        Payment payment = builder()
                .status(PaymentStatus.COMPLETE)
                .build();

        //when //then
        assertThatThrownBy(payment::complete)
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PAYMENT_ALREADY_COMPLETE);
    }

    @Test
    @DisplayName("결제 완료후 취소를 하면 환불이 된다.")
    void cancelPaymentWhenWait() {
        Payment payment = builder()
                .status(PaymentStatus.COMPLETE)
                .build();
        //when
        payment.cancel();

        //then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUND);
    }


}