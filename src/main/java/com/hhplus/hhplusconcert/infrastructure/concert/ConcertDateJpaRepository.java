package com.hhplus.hhplusconcert.infrastructure.concert;

import com.hhplus.hhplusconcert.domain.concert.entity.ConcertDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConcertDateJpaRepository extends JpaRepository<ConcertDate, Long> {

    List<ConcertDate> findAllByConcertInfo_concertId(Long concertId);

    Optional<ConcertDate> findByConcertDateIdAndConcertInfo_concertId(Long concertDateId, Long concertId);

    boolean existsConcertDateByConcertInfo_ConcertId(Long concertId);

    void deleteAllInBatch();
}
