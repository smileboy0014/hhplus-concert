package com.hhplus.hhplusconcert.domain.concert.entity;

import com.hhplus.hhplusconcert.domain.common.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "concert_date")
public class ConcertDate extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long concertDateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Concert concertInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Place placeInfo;

    private String concertDate;

}
