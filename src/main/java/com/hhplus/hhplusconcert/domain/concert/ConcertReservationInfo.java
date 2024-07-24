package com.hhplus.hhplusconcert.domain.concert;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.concert.command.ReservationCommand;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.RESERVATION_IS_ALREADY_CANCEL;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.RESERVATION_IS_NOT_READY_TO_RESERVE;
import static java.time.LocalDateTime.now;


@Builder(toBuilder = true)
@Getter
public class ConcertReservationInfo {

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

    public static ConcertReservationInfo toReservationDomain(ReservationCommand.Create command,
                                                             Seat seatForReservation,
                                                             ConcertDate dateForReservation) {

        return ConcertReservationInfo.builder()
                .concertId(command.concertId())
                .concertDateId(command.concertDateId())
                .userId(command.userId())
                .seatId(seatForReservation.getSeatId())
                .concertName(dateForReservation.getConcert().getName())
                .concertDate(dateForReservation.getConcertDate())
                .seatNumber(seatForReservation.getSeatNumber())
                .seatPrice(seatForReservation.getPrice())
                .build();
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
