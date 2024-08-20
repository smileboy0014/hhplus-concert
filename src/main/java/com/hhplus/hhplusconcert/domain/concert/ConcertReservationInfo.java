package com.hhplus.hhplusconcert.domain.concert;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.RESERVATION_IS_ALREADY_CANCEL;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.RESERVATION_IS_NOT_READY_TO_RESERVE;
import static java.time.LocalDateTime.now;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class ConcertReservationInfo implements Serializable {

    private Long reservationId;

    private Long concertId;

    private Long concertDateId;

    private Long userId;

    private Long seatId;

    private String concertName;

    private String concertDate;

    private int seatNumber;

    private BigDecimal seatPrice;

    private LocalDateTime reservedAt;

    private LocalDateTime createdAt;

    private ReservationStatus status; // 예약 취소 / 진행 중 / 예약 완료

    public enum ReservationStatus {
        COMPLETED, //예약 완료
        TEMPORARY_RESERVED, // 임시 예약 완료
        CANCEL // 예약 취소

    }

    public void complete() {
        if (status != ReservationStatus.TEMPORARY_RESERVED) {
            throw new CustomException(RESERVATION_IS_NOT_READY_TO_RESERVE,
                    "예약 완료할 수 없습니다.");
        }
        this.status = ReservationStatus.COMPLETED;
        this.reservedAt = now();
    }

    public void cancel() {
        if (status == ReservationStatus.TEMPORARY_RESERVED
                || status == ReservationStatus.COMPLETED) {
            status = ReservationStatus.CANCEL;
        } else {
            throw new CustomException(RESERVATION_IS_ALREADY_CANCEL,
                    "이미 예약이 취소되었습니다.");
        }
    }
}
