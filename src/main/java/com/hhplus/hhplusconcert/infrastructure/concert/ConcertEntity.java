package com.hhplus.hhplusconcert.infrastructure.concert;


import com.hhplus.hhplusconcert.domain.concert.Concert;
import com.hhplus.hhplusconcert.infrastructure.common.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "concert")
public class ConcertEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long concertId; // 콘서트 ID

    private String name; // 콘서트 이름

    public static ConcertEntity toEntity(Concert concert) {
        return ConcertEntity.builder()
                .concertId(concert.getConcertId())
                .name(concert.getName())
                .build();
    }

    public Concert toDomain() {
        return Concert
                .builder()
                .concertId(concertId)
                .name(name)
                .build();
    }
}