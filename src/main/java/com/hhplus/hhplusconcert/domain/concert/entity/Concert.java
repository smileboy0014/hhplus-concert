package com.hhplus.hhplusconcert.domain.concert.entity;


import com.hhplus.hhplusconcert.domain.common.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "concert")
public class Concert extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long concertId; // 콘서트 ID

    private String name; // 콘서트 이름

    @ManyToOne
    @JoinColumn(name = "place_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Place place;

}

