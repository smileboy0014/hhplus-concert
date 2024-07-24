package com.hhplus.hhplusconcert.domain.user.service;

import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.service.dto.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class UserReader {
    public UserInfo readUser(User user) {
        return UserInfo.of(user);
    }
}
