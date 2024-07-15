package com.hhplus.hhplusconcert.domain.concert.entity;

import com.hhplus.hhplusconcert.domain.common.model.BaseTimeEntity;
import com.hhplus.hhplusconcert.domain.concert.enums.SeatStatus;
import com.hhplus.hhplusconcert.domain.concert.enums.TicketClass;
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

    @Enumerated(EnumType.STRING)
    private SeatStatus status; // available, unavailable

    @Enumerated(EnumType.STRING)
    private TicketClass ticketClass; // S > A > B > C

    public void occupy() {
        this.status = SeatStatus.UNAVAILABLE;
    }

    public void cancel() {
        this.status = SeatStatus.AVAILABLE;
    }

    public void classS() {
        this.status = SeatStatus.AVAILABLE;
    }

    public void classA() {
        this.status = SeatStatus.UNAVAILABLE;
    }

    public void classB() {
        this.status = SeatStatus.AVAILABLE;
    }

    public void classC() {
        this.status = SeatStatus.AVAILABLE;
    }

}
