package com.hhplus.hhplusconcert.application.queue;

import com.hhplus.hhplusconcert.domain.queue.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.WaitingQueueService;
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
     * 토큰을 발급하고, 대기열 정보를 확인하는 유즈케이스를 실행한다.
     *
     * @param command userId, token 정보
     * @return WaitingQueueResponse 대기열 정보를 반환한다.
     */
    public WaitingQueue checkWaiting(WaitingQueueCommand.Create command) {
        // 유저 정보 조회
        User user = userService.getUser(command.userId());

        return waitingQueueService.checkWaiting(user, command.token());
    }

    /**
     * 대기열에 있는 토큰을 순차적으로 active 시키는 유즈케이스를 실행한다.
     */
    public void active() {
        waitingQueueService.activeTokens();
    }
}
