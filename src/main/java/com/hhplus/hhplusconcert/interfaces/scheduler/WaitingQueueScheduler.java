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

    /**
     * token을 active 하는 스케줄러 5초마다 실행
     */
    @Scheduled(fixedRate = 5000)
    public void activeToken() {
        waitingQueueFacade.active();
    }

    /**
     * token을 expired 하는 스케줄러 5초마다 실행
     */
    @Scheduled(fixedRate = 5000)
    public void expireToken() {
        waitingQueueFacade.expire();
    }

    /**
     * expired 된 토큰을 자정마다 삭제하는 스케줄러 실행
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteAllExpireWaitingQueue() {
        waitingQueueFacade.deleteAllExpireToken();
    }
}