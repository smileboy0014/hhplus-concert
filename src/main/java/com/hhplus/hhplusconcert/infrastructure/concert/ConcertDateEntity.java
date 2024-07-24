package com.hhplus.hhplusconcert.infrastructure.concert;

import com.hhplus.hhplusconcert.domain.concert.ConcertDate;
import com.hhplus.hhplusconcert.infrastructure.common.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "concert_date")
public class ConcertDateEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long concertDateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ConcertEntity concertInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private PlaceEntity placeInfo;

    private String concertDate;

    public static ConcertDateEntity toEntity(ConcertDate concertDate) {

        return ConcertDateEntity.builder()
                .concertDateId(concertDate.getConcertDateId())
                .concertInfo(ConcertEntity.toEntity(concertDate.getConcert()))
                .placeInfo(PlaceEntity.toEntity(concertDate.getPlace()))
                .concertDate(concertDate.getConcertDate())
                .build();
    }

    public ConcertDate toDomain() {

        return ConcertDate
                .builder()
                .concertDateId(concertDateId)
                .concert(concertInfo.toDomain())
                .place(placeInfo.toDomain())
                .concertDate(concertDate)
                .build();
    }
}
