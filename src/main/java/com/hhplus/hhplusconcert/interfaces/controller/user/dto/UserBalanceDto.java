package com.hhplus.hhplusconcert.interfaces.controller.user.dto;

import com.hhplus.hhplusconcert.domain.user.User;
import com.hhplus.hhplusconcert.domain.user.command.UserCommand;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;


public class UserBalanceDto {

    @Builder(toBuilder = true)
    public record Request(@Positive(message = "0 이상의 포인트를 충전 가능합니다.") BigDecimal balance) {

        public UserCommand.Create toCreateCommand(Long userId) {
            return new UserCommand.Create(userId, balance);
        }

    }

    @Builder(toBuilder = true)
    public record Response(Long userId, BigDecimal balance) {

        public static UserBalanceDto.Response of(User user) {
            return UserBalanceDto.Response.builder()
                    .userId(user.getUserId())
                    .balance(user.getBalance())
                    .build();
        }
    }


}
