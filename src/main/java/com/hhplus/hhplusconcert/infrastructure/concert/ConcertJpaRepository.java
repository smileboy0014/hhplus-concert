package com.hhplus.hhplusconcert.infrastructure.concert;

import com.hhplus.hhplusconcert.domain.concert.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {

    void deleteAllInBatch();

}
