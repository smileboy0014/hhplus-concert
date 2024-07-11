package com.hhplus.hhplusconcert.domain.concert.repository;

import com.hhplus.hhplusconcert.domain.concert.entity.Place;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository {

    // Place 관련
    Place addPlace(Place place);

    void deleteAll();

}
