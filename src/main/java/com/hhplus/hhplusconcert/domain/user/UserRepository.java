package com.hhplus.hhplusconcert.domain.user;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository {
    // User 관련
    Optional<User> getUser(Long reservationId);

    void deleteAll();

    Optional<User> saveUser(User user);

    List<User> getUsers();


}
