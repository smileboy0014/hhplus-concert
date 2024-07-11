package com.hhplus.hhplusconcert.domain.queue.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum WaitingQueueStatus {

    WAIT("wait"),
    ACTIVE("active"),
    EXPIRED("expired");


    private final String status;
}
