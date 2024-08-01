package com.hhplus.hhplusconcert.domain.concert;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.SEAT_CAN_RESERVE;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.SEAT_IS_UNAVAILABLE;


@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class Seat {

    private Long seatId;

    private ConcertDate concertDateInfo;

    private int seatNumber;

    private BigDecimal price;

    private SeatStatus status; // available, unavailable

    private TicketClass ticketClass; // S > A > B > C

    private int version;

    private LocalDateTime createdAt;


    public enum SeatStatus {
        AVAILABLE, // 이용 가능
        UNAVAILABLE // 이용 불가능
    }

    public enum TicketClass {
        C, // C class
        B, // B class
        A, // A class
        S // S class
    }

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

}
