package com.hhplus.hhplusconcert.infrastructure.user;

import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User addUser(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll();
    }

    @Override
    public Optional<User> findUserByUserId(Long userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public Optional<User> findUserByUserIdWithLock(Long userId) {
        return userJpaRepository.findUserByUserIdWithLock(userId);
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return userJpaRepository.existsByUserId(userId);
    }

    @Override
    public void deleteAll() {
        userJpaRepository.deleteAllInBatch();
    }


}
