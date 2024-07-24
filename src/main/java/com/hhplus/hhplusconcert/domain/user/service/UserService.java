package com.hhplus.hhplusconcert.domain.user.service;

import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.service.dto.UserInfo;
import com.hhplus.hhplusconcert.domain.user.service.dto.UserServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserFinder userFinder;
    private final UserReader userReader;

    /**
     * 잔액 조회를 요청하면 잔액 정보를 반환한다.
     *
     * @param userId userId 정보
     * @return UserResponse 유저의 잔액 정보를 반환한다.
     */
    public UserInfo getBalance(Long userId) {
        User user = userFinder.findUserByUserId(userId);

        return userReader.readUser(user);
    }

    /**
     * 잔액 충전을 요청하면 현재 잔액 정보를 반환한다.
     *
     * @param request userId, balance 정보
     * @return UserResponse 유저의 잔액 정보를 반환한다.
     */
    @Transactional
    public UserInfo chargeBalance(UserServiceRequest request) {
        // 유저 정보를 조회
        User user = userFinder.findUserByUserIdWithLock(request.userId());

        user.chargeBalance(request.balance());

        return userReader.readUser(user);
    }
}
