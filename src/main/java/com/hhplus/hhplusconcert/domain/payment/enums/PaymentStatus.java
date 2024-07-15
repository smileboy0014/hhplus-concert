package com.hhplus.hhplusconcert.domain.payment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentStatus {

    WAIT("wait"),
    COMPLETE("complete"),
    CANCEL("cancel"),
    REFUND("refund");

    private final String status;
}
