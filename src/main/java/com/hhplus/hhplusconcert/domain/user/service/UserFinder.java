package com.hhplus.hhplusconcert.domain.user.service;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.USER_IS_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class UserFinder {

    private final UserRepository userRepository;
    private final UserValidator userValidator;

    public List<User> findUsers() {
        return userRepository.findAll();
    }

    public User findUserByUserId(Long userId) {
        return userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new CustomException(USER_IS_NOT_FOUND,
                        "유저 정보가 존재하지 않습니다. [userId : %d]".formatted(userId)));
    }

    public User findUserByUserIdWithLock(Long userId) {
        return userRepository.findUserByUserIdWithLock(userId)
                .orElseThrow(() -> new CustomException(USER_IS_NOT_FOUND,
                        "유저 정보가 존재하지 않습니다. [userId : %d]".formatted(userId)));
    }

    public void existsUserByUserId(Long userId) {
        boolean result = userRepository.existsByUserId(userId);

        userValidator.validUser(result, userId);
    }


}
