package com.hhplus.hhplusconcert.domain.concert.entity;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.common.model.BaseTimeEntity;
import com.hhplus.hhplusconcert.domain.concert.enums.SeatStatus;
import com.hhplus.hhplusconcert.domain.concert.enums.TicketClass;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.SEAT_CAN_RESERVE;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.SEAT_IS_UNAVAILABLE;

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
        if (status == SeatStatus.UNAVAILABLE) {
            throw new CustomException(SEAT_IS_UNAVAILABLE,
                    "이미 해당 좌석의 예약 내역이 존재합니다. [seatNumber : %d]".formatted(seatNumber));
        }
        this.status = SeatStatus.UNAVAILABLE;
    }

    public void cancel() {
        if (status == SeatStatus.AVAILABLE) {
            throw new CustomException(SEAT_CAN_RESERVE,
                    "이미 예약 가능한 좌석입니다. [seatNumber : %d]".formatted(seatNumber));
        }
        this.status = SeatStatus.AVAILABLE;
    }

    public void classS() {
        this.ticketClass = TicketClass.S;
    }

    public void classA() {
        this.ticketClass = TicketClass.A;
    }

    public void classB() {
        this.ticketClass = TicketClass.B;
    }

    public void classC() {
        this.ticketClass = TicketClass.C;
    }

}
