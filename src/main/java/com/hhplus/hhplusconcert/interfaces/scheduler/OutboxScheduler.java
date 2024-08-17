package com.hhplus.hhplusconcert.interfaces.scheduler;

import com.hhplus.hhplusconcert.domain.outbox.Outbox;
import com.hhplus.hhplusconcert.domain.outbox.OutboxRepository;
import com.hhplus.hhplusconcert.domain.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.hhplus.hhplusconcert.support.config.KafkaTopicConfig.KafkaConstants.PAYMENT_TOPIC;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final EventProducer eventProducer;

    @Scheduled(fixedDelay = 10 * 1000) // 10초마다 실행
    public void retryFailEvent() {
        // Outbox 테이블에서 재시도가 필요한 메시지 조회
        List<Outbox> retryOutboxes = outboxRepository.getRetryOutboxes();
        if (retryOutboxes.isEmpty()) {
            return;
        }

        for (Outbox outbox : retryOutboxes) {
            if (outbox.getRetryCount() >= 3) {
                // 이미 3회 이상 재시도한 메시지 실패 처리
                outbox.fail();
                outboxRepository.saveOutbox(outbox);
                continue;
            }
            try {
                // 재시도 로직, outbox message 발행 완료 변경, KafkaProducer 를 통해 메시지 재발행
                outbox.publish();
                outboxRepository.saveOutbox(outbox);
                eventProducer.publish(PAYMENT_TOPIC, outbox.getOutboxId(), outbox.getPayload());

            } catch (Exception e) {
                log.error("send retry exception -> outboxId: {}, error: {}", outbox.getOutboxId(), e.getMessage());
                outbox.restore();
                outboxRepository.saveOutbox(outbox);
            }
            // 재시도 횟수 추가
            outbox.plusRetryCount();
            outboxRepository.saveOutbox(outbox);
        }
    }
}
