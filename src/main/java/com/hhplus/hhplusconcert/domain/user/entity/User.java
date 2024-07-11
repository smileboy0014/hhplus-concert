package com.hhplus.hhplusconcert.domain.user.entity;

import com.hhplus.hhplusconcert.domain.common.exception.CustomBadRequestException;
import com.hhplus.hhplusconcert.domain.common.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.CHARGE_AMOUNT_IS_NEGATIVE;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.NOT_ENOUGH_BALANCE;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private BigDecimal balance;

    public void chargeBalance(BigDecimal amount) {
        if (amount.signum() < 0) throw new CustomBadRequestException(CHARGE_AMOUNT_IS_NEGATIVE,
                "0 이상의 포인트를 충전 가능합니다.");

        this.balance = balance.add(amount);
    }

    public void useBalance(BigDecimal amount) {
        this.balance = balance.subtract(amount);
    }

    public void checkBalance(BigDecimal seatPrice) {
        if (seatPrice.compareTo(balance) > 0) throw new CustomBadRequestException(NOT_ENOUGH_BALANCE,
                "충전하려는 잔액이 충분하지 않습니다.");
    }
}
