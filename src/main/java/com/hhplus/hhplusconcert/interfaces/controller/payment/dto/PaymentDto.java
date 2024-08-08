package com.hhplus.hhplusconcert.interfaces.controller.payment.dto;


import com.hhplus.hhplusconcert.domain.payment.Payment;
import com.hhplus.hhplusconcert.domain.payment.command.PaymentCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

public class PaymentDto {

    @Builder(toBuilder = true)
    public record Request(@NotNull Long reservationId, @NotNull Long userId, @NotBlank String token) {
        public PaymentCommand.Create toCreateCommand() {
            return new PaymentCommand.Create(reservationId, userId, token);
        }
    }

    @Builder(toBuilder = true)
    public record Response(
            Long paymentId,
            Payment.PaymentStatus status,
            BigDecimal paymentPrice,
            BigDecimal balance
    ) {

        public static PaymentDto.Response of(Payment payment) {
            return Response.builder()
                    .paymentId(payment.getPaymentId())
                    .status(payment.getStatus())
                    .paymentPrice(payment.getPaymentPrice())
                    .balance(payment.getBalance())
                    .build();
        }
    }


}
