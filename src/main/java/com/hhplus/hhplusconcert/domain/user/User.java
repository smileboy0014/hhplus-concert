package com.hhplus.hhplusconcert.domain.user;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.USER_CHARGE_AMOUNT_IS_NEGATIVE;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.USER_NOT_ENOUGH_BALANCE;


@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private BigDecimal balance;

    private LocalDateTime createdAt;

//    private int version; //낙관적 락 적용

    public void chargeBalance(BigDecimal point) {
        if (point.signum() < 0) throw new CustomException(USER_CHARGE_AMOUNT_IS_NEGATIVE,
                "0 이상의 포인트를 충전 가능합니다.");
        this.balance = balance.add(point);
    }

    public void useBalance(BigDecimal point) {
        if (point.compareTo(balance) > 0) throw new CustomException(USER_NOT_ENOUGH_BALANCE,
                "충전하려는 잔액이 충분하지 않습니다.");
        this.balance = balance.subtract(point);
    }

}
