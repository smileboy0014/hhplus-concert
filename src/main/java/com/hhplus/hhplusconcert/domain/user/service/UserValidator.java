package com.hhplus.hhplusconcert.domain.user.service;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import org.springframework.stereotype.Component;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.USER_IS_NOT_FOUND;

@Component
public class UserValidator {

    public void validUser(boolean result, Long userId) {
        if (!result) throw new CustomException(USER_IS_NOT_FOUND,
                "유저 정보가 존재하지 않습니다. [userId : %d]".formatted(userId));
    }
}