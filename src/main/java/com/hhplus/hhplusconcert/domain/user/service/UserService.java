package com.hhplus.hhplusconcert.domain.user.service;

import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.repository.UserRepository;
import com.hhplus.hhplusconcert.domain.user.service.dto.UserInfo;
import com.hhplus.hhplusconcert.domain.user.service.dto.UserServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * 잔액 조회를 요청하면 잔액 정보를 반환한다.
     *
     * @param userId userId 정보
     * @return UserResponse 유저의 잔액 정보를 반환한다.
     */
    public UserInfo getBalance(Long userId) {

        return UserInfo.of(userRepository.findUserByUserId(userId));
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
        User user = userRepository.findUserByUserId(request.userId());
        // 유저 포인트 충전
        user.chargeBalance(request.balance());

        return UserInfo.of(user);
    }
}
