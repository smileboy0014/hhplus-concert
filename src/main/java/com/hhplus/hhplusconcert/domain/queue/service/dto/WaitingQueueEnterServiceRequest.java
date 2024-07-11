package com.hhplus.hhplusconcert.domain.queue.service.dto;

import com.hhplus.hhplusconcert.domain.queue.entity.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.enums.WaitingQueueStatus;
import com.hhplus.hhplusconcert.domain.user.entity.User;
import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record WaitingQueueEnterServiceRequest(Long userId,
                                              String token) {

    public WaitingQueue toEntity(User user, String status) {
        return WaitingQueue
                .builder()
                .user(user)
                .token(token)
                .status(status)
                .requestTime(new Timestamp(System.currentTimeMillis()))
                .activeTime(status.equals(WaitingQueueStatus.ACTIVE.getStatus()) ? new Timestamp(System.currentTimeMillis())
                        : null)
                .build();

    }
}
