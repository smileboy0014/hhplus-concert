package com.hhplus.hhplusconcert.domain.concert.service.dto;

import com.hhplus.hhplusconcert.domain.concert.entity.Reservation;
import com.hhplus.hhplusconcert.domain.concert.enums.ReservationStatus;
import lombok.Builder;

@Builder
public record ReservationInfo(Long reservationId, ReservationStatus status,
                              ReservationConcertInfo concertInfo,
                              ReservationPaymentInfo paymentInfo) {
    public static ReservationInfo of(Reservation reservation, ReservationConcertInfo concert,
                                     ReservationPaymentInfo payment) {

        return ReservationInfo.builder()
                .reservationId(reservation.getReservationId())
                .status(reservation.getStatus())
                .concertInfo(concert)
                .paymentInfo(payment)
                .build();

    }
}
