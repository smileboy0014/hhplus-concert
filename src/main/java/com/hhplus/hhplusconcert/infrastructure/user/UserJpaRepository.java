package com.hhplus.hhplusconcert.infrastructure.user;

import com.hhplus.hhplusconcert.domain.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    boolean existsByUserId(Long userId);

    //    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Lock(LockModeType.OPTIMISTIC) //여기
    @Query("SELECT u FROM User u WHERE u.userId = :userId")
    Optional<User> findUserByUserIdWithLock(@Param("userId") Long userId);
}
