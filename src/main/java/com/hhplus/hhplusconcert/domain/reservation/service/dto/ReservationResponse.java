package com.hhplus.hhplusconcert.domain.reservation.service.dto;

import lombok.Builder;

@Builder
public record ReservationResponse(Long reservationId, String status,
                                  ReservationConcertResponse concertInfo,
                                  ReservationPaymentResponse paymentInfo) {
}
