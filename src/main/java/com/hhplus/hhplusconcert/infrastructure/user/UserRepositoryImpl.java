package com.hhplus.hhplusconcert.infrastructure.user;

import com.hhplus.hhplusconcert.domain.common.exception.CustomNotFoundException;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import com.hhplus.hhplusconcert.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.USER_IS_NOT_FOUND;

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
    public User findUserByUserId(Long userId) {
        return userJpaRepository.findById(userId)
                .orElseThrow(() -> new CustomNotFoundException(USER_IS_NOT_FOUND,
                        "유저 정보가 존재하지 않습니다. [userId : %d]".formatted(userId)));
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
