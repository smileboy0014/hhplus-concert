package com.hhplus.hhplusconcert.infrastructure.user;

import com.hhplus.hhplusconcert.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    boolean existsByUserId(Long userId);
}
