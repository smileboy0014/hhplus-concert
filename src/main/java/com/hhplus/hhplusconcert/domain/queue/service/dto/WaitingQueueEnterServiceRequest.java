package com.hhplus.hhplusconcert.domain.queue.service.dto;

import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record WaitingQueueEnterServiceRequest(Long userId,
                                              String token) {

    public WaitingQueue toEntity(User user, WaitingQueueStatus status) {
        return WaitingQueue
                .builder()
                .user(user)
                .token(token)
                .status(status)
                .requestTime(LocalDateTime.now())
                .activeTime(status == WaitingQueueStatus.ACTIVE ? LocalDateTime.now() : null)
                .build();

    }
}
