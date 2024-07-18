package com.hhplus.hhplusconcert.domain.user.service;

import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAppender {
    private final UserRepository userRepository;

    public User appendUser(User user) {
        return userRepository.addUser(user);
    }


    public void deleteAll() {
        userRepository.deleteAll();
    }
}
