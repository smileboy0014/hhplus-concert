package com.hhplus.hhplusconcert.infrastructure.concert;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceJpaRepository extends JpaRepository<PlaceEntity, Long> {

    void deleteAllInBatch();
}
