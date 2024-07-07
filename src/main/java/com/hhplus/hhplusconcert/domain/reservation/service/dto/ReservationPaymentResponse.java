package com.hhplus.hhplusconcert.domain.reservation.service.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ReservationPaymentResponse(Long paymentId, String status,
                                         BigDecimal paymentPrice) {
}
