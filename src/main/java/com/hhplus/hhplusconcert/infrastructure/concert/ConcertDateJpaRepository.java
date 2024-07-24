package com.hhplus.hhplusconcert.infrastructure.concert;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConcertDateJpaRepository extends JpaRepository<ConcertDateEntity, Long> {

    @Query("SELECT cd FROM ConcertDateEntity cd " +
            "JOIN FETCH cd.concertInfo ci " +
            "JOIN FETCH cd.placeInfo pi " +
            "WHERE ci.concertId = :concertId " +
            "AND cd.concertDate > :concertDate")
    List<ConcertDateEntity> findAllByConcertInfo_concertIdAndConcertDateAfter(@Param("concertId") Long concertId,
                                                                              @Param("concertDate") String concertDate);

    Optional<ConcertDateEntity> findByConcertDateIdAndConcertInfo_concertId(Long concertDateId, Long concertId);

    void deleteAllInBatch();

}
