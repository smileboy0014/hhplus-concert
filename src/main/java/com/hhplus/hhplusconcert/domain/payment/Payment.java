package com.hhplus.hhplusconcert.domain.payment;


import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.concert.ConcertReservationInfo;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.PAYMENT_ALREADY_CANCEL_OR_REFUND;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.PAYMENT_ALREADY_COMPLETE;


@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@ToString
public class Payment {

    private Long paymentId;

    private ConcertReservationInfo concertReservationInfo;

    private BigDecimal paymentPrice;

    private BigDecimal balance;

    private PaymentStatus status; // 대기 / 완료 / 최소 / 환불

    private LocalDateTime paidAt;

    private LocalDateTime createdAt;

    public enum PaymentStatus {

        COMPLETE, // 결제 완료
        CANCEL, // 결제 취소 완료
        REFUND // 결제 환불 완료

    }

    public void complete() {
        if (status == PaymentStatus.COMPLETE) {
            throw new CustomException(PAYMENT_ALREADY_COMPLETE,
                    "이미 결제되었습니다.");
        }
        status = PaymentStatus.COMPLETE;
    }

    public void cancel() {
        if (status == PaymentStatus.COMPLETE) {
            status = PaymentStatus.REFUND;
        } else {
            throw new CustomException(PAYMENT_ALREADY_CANCEL_OR_REFUND,
                    "이미 취소됐거나 환불된 결제 정보입니다.");
        }
    }
}

