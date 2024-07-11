package com.hhplus.hhplusconcert.domain.concert.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TicketClass {

    C("C"),
    B("B"),
    A("A"),
    S("S");

    private final String degree;
}
