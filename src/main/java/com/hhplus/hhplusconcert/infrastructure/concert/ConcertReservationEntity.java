package com.hhplus.hhplusconcert.infrastructure.concert;

import com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo;
import com.hhplus.hhplusconcert.infrastructure.common.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo.ReservationStatus;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "reservation", uniqueConstraints = {
        @UniqueConstraint(
                name="UNIQUE_RESERVATION",
                columnNames={"concert_id","concert_date_id","seat_number"})
})
public class ConcertReservationEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @Column(nullable = false)
    private Long concertId;

    @Column(nullable = false)
    private Long concertDateId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long seatId;

    private String concertName;

    private String concertDate;

    private int seatNumber;

    private BigDecimal seatPrice;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status; // 예약 취소 / 진행 중 / 예약 완료


    private LocalDateTime reservedAt; // 예약이 완료되었을 때만

    @CreatedDate
    private LocalDateTime createdAt;

    public static ConcertReservationEntity toEntity(ConcertReservationInfo reservation) {
        return ConcertReservationEntity.builder()
                .reservationId(reservation.getReservationId() != null ? reservation.getReservationId() :
                        null)
                .status(reservation.getStatus() != null ? reservation.getStatus() : ReservationStatus.TEMPORARY_RESERVED)
                .concertId(reservation.getConcertId())
                .concertDateId(reservation.getConcertDateId())
                .concertName(reservation.getConcertName())
                .concertDate(reservation.getConcertDate())
                .seatId(reservation.getSeatId())
                .seatNumber(reservation.getSeatNumber())
                .seatPrice(reservation.getSeatPrice())
                .userId(reservation.getUserId())
                .reservedAt(reservation.getReservedAt() != null ? reservation.getReservedAt() : null)
                .createdAt(reservation.getCreatedAt())
                .build();
    }


    public ConcertReservationInfo toDomain() {
        return ConcertReservationInfo.builder()
                .reservationId(reservationId)
                .status(status)
                .concertId(concertId)
                .concertDateId(concertDateId)
                .userId(userId)
                .seatId(seatId)
                .concertName(concertName)
                .concertDate(concertDate)
                .seatNumber(seatNumber)
                .seatPrice(seatPrice)
                .createdAt(createdAt)
                .build();
    }
}
