package com.hhplus.hhplusconcert.domain.queue.command;

public class TokenCommand {

    public record Create(Long userId, String token) {
    }
}
