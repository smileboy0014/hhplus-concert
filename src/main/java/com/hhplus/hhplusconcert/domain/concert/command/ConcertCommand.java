package com.hhplus.hhplusconcert.domain.concert.command;

public class ConcertCommand {

    public record Create(
            String concertName
    ) {
    }
}
