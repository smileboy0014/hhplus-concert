package com.hhplus.hhplusconcert.domain.payment.listener;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.outbox.Outbox;
import com.hhplus.hhplusconcert.domain.outbox.OutboxRepository;
import com.hhplus.hhplusconcert.domain.payment.event.PaymentEvent;
import com.hhplus.hhplusconcert.domain.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.OUTBOX_IS_FAILED;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.OUTBOX_IS_NOT_FOUND;
import static com.hhplus.hhplusconcert.support.config.KafkaTopicConfig.KafkaConstants.PAYMENT_TOPIC;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final OutboxRepository outboxRepository;
    private final EventProducer eventProducer;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOutboxPayment(PaymentEvent event) {
        // Outbox data 생성
        Outbox outbox = Outbox.builder()
                .type(Outbox.EventType.PAYMENT)
                .status(Outbox.EventStatus.INIT)
                .payload(String.valueOf(event.getPayment().getPaymentId()))
                .build();

        Optional<Outbox> savedOutbox = outboxRepository.saveOutbox(outbox);

        if (savedOutbox.isEmpty()) throw new CustomException(OUTBOX_IS_FAILED, OUTBOX_IS_FAILED.getMsg());
        // set outBoxId
        event.setOutboxId(savedOutbox.get().getOutboxId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPaymentEvent(PaymentEvent event) {
        Optional<Outbox> outbox = outboxRepository.getOutbox(event.getOutboxId());
        if (outbox.isEmpty()) throw new CustomException(OUTBOX_IS_NOT_FOUND, OUTBOX_IS_NOT_FOUND.getMsg());
        // outbox 상태 변경
        outbox.get().publish();
        outboxRepository.saveOutbox(outbox.get());
        // 이벤트 메시지 발행
        eventProducer.publish(PAYMENT_TOPIC, event.getOutboxId(), String.valueOf(event.getPayment().getPaymentId()));
    }
}
