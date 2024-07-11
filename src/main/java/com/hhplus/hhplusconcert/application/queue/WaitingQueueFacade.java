package com.hhplus.hhplusconcert.application.queue;

import com.hhplus.hhplusconcert.domain.queue.service.WaitingQueueService;
import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueEnterServiceRequest;
import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueResponse;
import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueTokenResponse;
import com.hhplus.hhplusconcert.domain.queue.service.dto.WaitingQueueTokenServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaitingQueueFacade {

    private final WaitingQueueService waitingQueueService;

    /**
     * 토큰 발급 요청하는 유즈케이스를 실행한다.
     *
     * @param request userId 정보
     * @return WaitingQueueTokenResponse 토큰 정보를 반환한다.
     */
    public WaitingQueueTokenResponse issueToken(WaitingQueueTokenServiceRequest request) {
        return waitingQueueService.issueToken(request);
    }

    /**
     * 대기열에 진입하는 유즈케이스를 실행한다.
     *
     * @param request userId, token 정보
     * @return WaitingQueueResponse 대기열 정보를 반환한다.
     */
    public WaitingQueueResponse enterQueue(WaitingQueueEnterServiceRequest request) {
        return waitingQueueService.enterQueue(request);
    }

    /**
     * 대기열 정보를 확인하는 유즈케이스를 실행한다.
     *
     * @param request userId, token 정보
     * @return WaitingQueueResponse 대기열 정보를 반환한다.
     */
    public WaitingQueueResponse checkQueue(WaitingQueueEnterServiceRequest request) {
        return waitingQueueService.checkQueue(request);
    }

    /**
     * 만료시간이 된 토큰을 만료시키고, 대기열에 있는 토큰을 순차적으로 active 시키는 유즈케이스를 실행한다.
     */
    public void activeToken() {
        waitingQueueService.activeToken();
    }

    /**
     * 만료된 토큰을 삭제하는 유즈케이스를 실행한다.
     */
    public void deleteAllExpireToken() {
        waitingQueueService.deleteAllExpireToken();
    }
}