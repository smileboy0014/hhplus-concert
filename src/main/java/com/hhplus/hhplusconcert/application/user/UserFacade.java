package com.hhplus.hhplusconcert.application.user;

import com.hhplus.hhplusconcert.domain.user.service.UserService;
import com.hhplus.hhplusconcert.domain.user.service.dto.UserResponse;
import com.hhplus.hhplusconcert.domain.user.service.dto.UserServiceRequest;
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
    public UserResponse getBalance(Long userId) {
        return userService.getBalance(userId);
    }

    /**
     * 잔액을 충전하는 요청하는 유즈케이스를 실행한다.
     *
     * @param request userId, balance 정보
     * @return UserResponse 유저의 잔액 정보를 반환한다.
     */
    public UserResponse chargeBalance(UserServiceRequest request) {
        return userService.chargeBalance(request);
    }
}
