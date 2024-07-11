package com.hhplus.hhplusconcert.domain.concert.entity;

import com.hhplus.hhplusconcert.domain.common.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "seat")
public class Seat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_date_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ConcertDate concertDateInfo;

    private int seatNumber;

    private BigDecimal price;

    private String status; // available, unavailable

    private String ticketClass; // S > A > B > C

    public void changeStatus(String status) {
        this.status = status;
    }

}
