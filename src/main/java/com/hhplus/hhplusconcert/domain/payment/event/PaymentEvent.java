package com.hhplus.hhplusconcert.domain.payment.event;

import com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo;
import com.hhplus.hhplusconcert.domain.outbox.command.OutboxCommand;
import com.hhplus.hhplusconcert.domain.payment.Payment;
import com.hhplus.hhplusconcert.support.utils.JsonUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

import static com.hhplus.hhplusconcert.domain.outbox.Outbox.DomainType;
import static com.hhplus.hhplusconcert.domain.outbox.Outbox.EventStatus;

@Getter
@Setter
@ToString
public class PaymentEvent extends ApplicationEvent {

    public PaymentEvent() {
        super("");
    }


    private ConcertReservationInfo reservationInfo;
    private String messageId;
    private Payment payment;
    private String token;

    public PaymentEvent(Object source, ConcertReservationInfo reservationInfo,
                        Payment payment, String token) {
        super(source);
        this.reservationInfo = reservationInfo;
        this.payment = payment;
        this.token = token;
    }

    public OutboxCommand.Create toOutboxPaymentCommand() {
        String uuid = UUID.randomUUID().toString();
        this.messageId = uuid;
        return new OutboxCommand.Create(uuid, DomainType.PAYMENT, EventStatus.INIT, JsonUtils.toJson(this));
    }
}
