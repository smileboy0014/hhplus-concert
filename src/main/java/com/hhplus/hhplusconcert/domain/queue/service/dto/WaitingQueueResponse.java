package com.hhplus.hhplusconcert.domain.queue.service.dto;

import lombok.Builder;

@Builder
public record WaitingQueueResponse(Long userId, boolean isActive,
                                   WaitingQueueInfoResponse waitingInfo) {
}
