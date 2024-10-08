package com.hhplus.hhplusconcert.infrastructure.user;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByUserId(Long userId);

    //    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Lock(LockModeType.OPTIMISTIC) //여기
    @Query("SELECT u FROM UserEntity u WHERE u.userId = :userId")
    Optional<UserEntity> findUserByUserIdWithLock(@Param("userId") Long userId);
}
