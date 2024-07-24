package com.hhplus.hhplusconcert.interfaces.controller.queue.dto;

import com.hhplus.hhplusconcert.domain.queue.command.TokenCommand;
import lombok.Builder;

public class TokenDto {

    @Builder(toBuilder = true)
    public record Request(Long userId) {
        public TokenCommand.Create toCreateCommand() {
            return new TokenCommand.Create(userId);
        }
    }
}
