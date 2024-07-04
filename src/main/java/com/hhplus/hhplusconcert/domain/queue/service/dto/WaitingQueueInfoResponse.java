package com.hhplus.hhplusconcert.domain.queue.service.dto;

import lombok.Builder;

@Builder
public record WaitingQueueInfoResponse(int waitingNumber, int expectedWaitingTime) {
}
