package com.hhplus.hhplusconcert.domain.user.repository;

import com.hhplus.hhplusconcert.domain.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository {
    // User 관련
    List<User> findAll();

    User addUser(User user);

    Optional<User> findUserByUserId(Long userId);

    boolean existsByUserId(Long userId);

    void deleteAll();

    Optional<User> findUserByUserIdWithLock(Long userId);
}
