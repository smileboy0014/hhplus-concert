package com.hhplus.hhplusconcert.domain.queue.service;

import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.repository.WaitingQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaitingQueueAppender {
    private final WaitingQueueRepository waitingQueueRepository;

    public WaitingQueue appendWaitingQueue(WaitingQueue waitingQueue) {
        return waitingQueueRepository.save(waitingQueue);

    }

    public void deleteAllExpireToken() {
        waitingQueueRepository.deleteAllExpireToken();
    }

    public void deleteAll() {
        waitingQueueRepository.deleteAll();
    }
}
