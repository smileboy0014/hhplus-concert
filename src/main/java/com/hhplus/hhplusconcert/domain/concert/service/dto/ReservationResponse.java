package com.hhplus.hhplusconcert.domain.concert.service.dto;

import com.hhplus.hhplusconcert.domain.concert.entity.Reservation;
import com.hhplus.hhplusconcert.domain.payment.entity.Payment;
import lombok.Builder;

@Builder
public record ReservationResponse(Long reservationId, String status,
                                  ReservationConcertResponse concertInfo,
                                  ReservationPaymentResponse paymentInfo) {
    public static ReservationResponse of(Reservation reservation, ReservationConcertResponse concert,
                                         ReservationPaymentResponse payment) {

        return ReservationResponse.builder()
                .reservationId(reservation.getReservationId())
                .status(reservation.getStatus())
                .concertInfo(concert)
                .paymentInfo(payment)
                .build();

    }

    public void setPaymentInfo(Payment payment) {

    }
}
