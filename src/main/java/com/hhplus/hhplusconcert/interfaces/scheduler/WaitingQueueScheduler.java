package com.hhplus.hhplusconcert.interfaces.scheduler;

import com.hhplus.hhplusconcert.application.queue.WaitingQueueFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WaitingQueueScheduler { // 대기열 관련 스케줄러

    private final WaitingQueueFacade waitingQueueFacade;

    @Scheduled(fixedRate = 5000) // 매 5초마다 스케줄러 실행
    public void activeToken() {
        log.info("token을 active하는 스케줄러 실행");
        waitingQueueFacade.active();
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정마다 스케줄러 실행
    public void deleteAllExpireWaitingQueue() {
        log.info("expired 된 토큰 삭제 스케줄러 실행");
        waitingQueueFacade.deleteAllExpireToken();
    }
}