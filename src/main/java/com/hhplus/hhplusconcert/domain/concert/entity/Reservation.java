package com.hhplus.hhplusconcert.domain.concert.entity;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.common.model.BaseTimeEntity;
import com.hhplus.hhplusconcert.domain.concert.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.RESERVATION_IS_ALREADY_CANCEL;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.RESERVATION_IS_NOT_READY_TO_RESERVE;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "reservation")
public class Reservation extends BaseTimeEntity {

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

    @Enumerated(EnumType.STRING)
    private ReservationStatus status; // 예약 취소 / 진행 중 / 예약 완료

    public void cancel() {
        if (status == ReservationStatus.TEMPORARY_RESERVED
                || status == ReservationStatus.COMPLETED) {
            status = ReservationStatus.CANCEL;
        } else {
            throw new CustomException(RESERVATION_IS_ALREADY_CANCEL,
                    "이미 예약이 취소되었습니다.");
        }
    }

    public void complete() {
        if (status != ReservationStatus.TEMPORARY_RESERVED) {
            throw new CustomException(RESERVATION_IS_NOT_READY_TO_RESERVE,
                    "예약 완료할 수 없습니다.");
        }
        this.status = ReservationStatus.COMPLETED;
    }

    @CreatedDate
    private LocalDateTime reservedAt;

}
