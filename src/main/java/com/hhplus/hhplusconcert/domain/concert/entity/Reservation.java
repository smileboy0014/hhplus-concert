package com.hhplus.hhplusconcert.domain.concert.entity;

import com.hhplus.hhplusconcert.domain.common.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

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

    private String status; // 예약 취소 / 진행 중 / 예약 완료

    @CreatedDate
    private LocalDateTime reservedAt;

    public void changeStatus(String status) {
        this.status = status;
    }


}
