package com.hhplus.hhplusconcert.interfaces.controller.queue.dto;

import com.hhplus.hhplusconcert.domain.queue.WaitingQueue;
import com.hhplus.hhplusconcert.domain.queue.command.WaitingQueueCommand;
import lombok.Builder;

import static com.hhplus.hhplusconcert.domain.queue.WaitingQueue.WaitingQueueStatus.ACTIVE;

public class WaitingQueueDto {

    @Builder(toBuilder = true)
    public record Request(Long userId, String token) {
        public WaitingQueueCommand.Create toCreateCommand() {
            return new WaitingQueueCommand.Create(userId, token);
        }
    }

    @Builder(toBuilder = true)
    public record Response(Long userId, String token, boolean isActive,
                           WaitingInfo waitingInfo) {

        public static WaitingQueueDto.Response of(WaitingQueue queue) {
            return Response.builder()
                    .userId(queue.getUser().getUserId())
                    .token(queue.getToken())
                    .isActive(queue.getStatus() == ACTIVE)
                    .waitingInfo(queue.getWaitingNum() != null ? new WaitingInfo(queue.getWaitingNum(), queue.getWaitTimeInSeconds()) : null)
                    .build();

        }

        public record WaitingInfo(
                long waitingNumber,
                long waitTimeInSeconds
        ) {
        }
    }
}
