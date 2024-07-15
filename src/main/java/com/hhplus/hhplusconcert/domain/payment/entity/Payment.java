package com.hhplus.hhplusconcert.domain.payment.entity;


import com.hhplus.hhplusconcert.domain.common.exception.CustomBadRequestException;
import com.hhplus.hhplusconcert.domain.common.model.BaseTimeEntity;
import com.hhplus.hhplusconcert.domain.concert.entity.Reservation;
import com.hhplus.hhplusconcert.domain.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.ALREADY_CANCEL_OR_REFUND;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.NOT_AVAILABLE_STATE_PAYMENT;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "payment")
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Reservation reservation;

    private BigDecimal paymentPrice;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // 대기 / 완료 / 최소 / 환불

    private LocalDateTime paidAt;

    public void complete() {
        status = PaymentStatus.COMPLETE;
    }

    public void cancel() {
        if (status == PaymentStatus.WAIT) {
            status = PaymentStatus.CANCEL;
        } else if (status == PaymentStatus.COMPLETE) {
            status = PaymentStatus.REFUND;
        } else {
            throw new CustomBadRequestException(ALREADY_CANCEL_OR_REFUND,
                    "이미 취소됐거나 환불된 결제 정보입니다.");
        }
    }

    public void checkStatus() {
        if (status != PaymentStatus.WAIT) throw new CustomBadRequestException(
                NOT_AVAILABLE_STATE_PAYMENT, "결제 가능한 상태가 아닙니다.");
    }

    public void payPrice() {
        this.paymentPrice = this.price;
        this.paidAt = LocalDateTime.now();
    }
}

