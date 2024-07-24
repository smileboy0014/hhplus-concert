package com.hhplus.hhplusconcert.infrastructure.payment;


import com.hhplus.hhplusconcert.domain.payment.Payment;
import com.hhplus.hhplusconcert.infrastructure.common.model.BaseTimeEntity;
import com.hhplus.hhplusconcert.infrastructure.concert.ConcertReservationEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.hhplus.hhplusconcert.domain.payment.Payment.PaymentStatus;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "payment")
public class PaymentEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ConcertReservationEntity concertReservation;

    private BigDecimal paymentPrice;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // 대기 / 완료 / 최소 / 환불

    private LocalDateTime paidAt; //결제가 완료됐을 때만

    @CreatedDate
    private LocalDateTime createdAt;

    public static PaymentEntity toEntity(Payment payment) {
        return PaymentEntity.builder()
                .paymentId(payment.getPaymentId() != null ? payment.getPaymentId() : null)
                .concertReservation(ConcertReservationEntity.toEntity(payment.getConcertReservationInfo()))
                .paymentPrice(payment.getPaymentPrice())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .paidAt(payment.getPaidAt() != null ? payment.getPaidAt() : null)
                .build();
    }

    public Payment toDomain() {
        return Payment
                .builder()
                .paymentId(paymentId)
                .concertReservationInfo(concertReservation.toDomain())
                .paymentPrice(paymentPrice)
                .status(status)
                .paidAt(paidAt)
                .createdAt(createdAt)
                .build();
    }

}

