package com.hhplus.hhplusconcert.interfaces.scheduler;

import com.hhplus.hhplusconcert.domain.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxService outboxService;

    @Scheduled(fixedDelay = 5 * 60 * 1000) // 5분 마다 실행
    public void retryFailEvent() {
        outboxService.retryFailMessage();
    }
}
