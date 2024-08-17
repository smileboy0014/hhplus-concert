package com.hhplus.hhplusconcert.domain.payment.event;

import com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo;
import com.hhplus.hhplusconcert.domain.payment.Payment;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
@ToString
public class PaymentEvent extends ApplicationEvent {
    private final ConcertReservationInfo reservationInfo;
    private final Payment payment;
    private Long outboxId;
    private final String token;

    public PaymentEvent(Object source, ConcertReservationInfo reservationInfo,
                        Payment payment, String token) {
        super(source);
        this.reservationInfo = reservationInfo;
        this.payment = payment;
        this.token = token;
    }
}
