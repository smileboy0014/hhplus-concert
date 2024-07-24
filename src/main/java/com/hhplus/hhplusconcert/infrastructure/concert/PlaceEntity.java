package com.hhplus.hhplusconcert.infrastructure.concert;


import com.hhplus.hhplusconcert.domain.concert.Place;
import com.hhplus.hhplusconcert.infrastructure.common.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "place")
public class PlaceEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    private String name;

    private int totalSeat;

    public Place toDomain() {
        return Place
                .builder()
                .placeId(placeId)
                .name(name)
                .totalSeat(totalSeat)
                .build();
    }

    public static PlaceEntity toEntity(Place place) {
        return PlaceEntity.builder()
                .placeId(place.getPlaceId())
                .name(place.getName())
                .totalSeat(place.getTotalSeat())
                .build();
    }
}