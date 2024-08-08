package com.hhplus.hhplusconcert.domain.payment.command;

public class PaymentCommand {
    public record Create(
            Long reservationId,
            Long userId,
            String token) {
    }

}
