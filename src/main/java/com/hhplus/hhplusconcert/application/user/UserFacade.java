package com.hhplus.hhplusconcert.application.user;

import com.hhplus.hhplusconcert.domain.user.User;
import com.hhplus.hhplusconcert.domain.user.UserService;
import com.hhplus.hhplusconcert.domain.user.command.UserCommand;
import com.hhplus.hhplusconcert.support.aop.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;

    /**
     * 잔액 조회를 요청하는 유즈케이스를 실행한다.
     *
     * @param userId userId 정보
     * @return UserResponse 유저의 잔액 정보를 반환한다.
     */
    public User getBalance(Long userId) {
        return userService.getUser(userId);
    }


    /**
     * 잔액을 충전하는 요청하는 유즈케이스를 실행한다.
     *
     * @param command userId, balance 정보
     * @return UserResponse 유저의 잔액 정보를 반환한다.
     */
    @DistributedLock(key = "'userLock'.concat(':').concat(#command.userId())")
    public User chargeBalance(UserCommand.Create command) {
        return userService.chargeBalance(command);
    }
}
