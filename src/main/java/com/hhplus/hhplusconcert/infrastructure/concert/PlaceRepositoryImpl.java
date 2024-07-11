package com.hhplus.hhplusconcert.infrastructure.concert;

import com.hhplus.hhplusconcert.domain.concert.entity.Place;
import com.hhplus.hhplusconcert.domain.concert.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepository {

    private final PlaceJpaRepository placeJpaRepository;

    @Override
    public Place addPlace(Place place) {
        return placeJpaRepository.save(place);
    }

    public void deleteAll() {
        placeJpaRepository.deleteAllInBatch();

    }
}
