package com.hhplus.hhplusconcert.domain.concert.entity;


import com.hhplus.hhplusconcert.domain.common.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "place")
public class Place extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    private String name;

    private int totalSeat;

}