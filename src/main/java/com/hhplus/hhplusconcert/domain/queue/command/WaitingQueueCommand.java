package com.hhplus.hhplusconcert.domain.queue.command;

public class WaitingQueueCommand {

    public record Create(Long userId, String token) {
    }

//    public static WaitingQueue toWaitingQueueDomain(Token token) {
//        return WaitingQueue.builder()
//                .token(token)
//                .build();
//    }

}
