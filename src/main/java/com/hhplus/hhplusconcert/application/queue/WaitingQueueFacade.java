package com.hhplus.hhplusconcert.application.queue;

import com.hhplus.hhplusconcert.domain.queue.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.WaitingQueueService;
import com.hhplus.hhplusconcert.domain.queue.command.TokenCommand;
import com.hhplus.hhplusconcert.domain.queue.command.WaitingQueueCommand;
import com.hhplus.hhplusconcert.domain.user.User;
import com.hhplus.hhplusconcert.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaitingQueueFacade {

    private final WaitingQueueService waitingQueueService;
    private final UserService userService;

    /**
     * 토큰 발급 요청하는 유즈케이스를 실행한다.
     *
     * @param command userId 정보
     * @return WaitingQueueTokenResponse 토큰 정보를 반환한다.
     */
    public WaitingQueue issueToken(TokenCommand.Create command) {
        // 유저 정보 조회
        User user = userService.getUser(command.userId());
        // 토큰 발급
        String token = waitingQueueService.issueToken(user.getUserId());
        // 대기열 진입 및 정보 반환
        return waitingQueueService.enterQueue(user, token);
    }

    /**
     * 대기열 정보를 확인하는 유즈케이스를 실행한다.
     *
     * @param command userId, token 정보
     * @return WaitingQueueResponse 대기열 정보를 반환한다.
     */
    public WaitingQueue checkQueue(WaitingQueueCommand.Create command) {

        return waitingQueueService.checkQueue(command.userId(), command.token());

    }

    /**
     * 대기열에 있는 토큰을 순차적으로 active 시키는 유즈케이스를 실행한다.
     */
    public void active() {
        waitingQueueService.activeToken(null);
    }

    /**
     * 시간이 만료된 active token 을 expired 시키는 유즈케이스를 실행한다.
     */
    public void expire() {
        waitingQueueService.expireToken();
    }

    /**
     * expired 된 토큰을 삭제하는 유즈케이스를 실행한다.
     */
    public void deleteAllExpireToken() {
        waitingQueueService.deleteExpiredToken();
    }
}
