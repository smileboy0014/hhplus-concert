package com.hhplus.hhplusconcert.domain.outbox;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.OUTBOX_IS_ALREADY_DONE;
import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.OUTBOX_IS_ALREADY_FAIL;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class Outbox {

    private Long outboxId;

    private EventType type;

    private EventStatus status;

    private String payload;

    private int retryCount;

    private LocalDateTime createdAt;

    public void publish() {
        if (status == EventStatus.DONE) {
            throw new CustomException(OUTBOX_IS_ALREADY_DONE,
                    OUTBOX_IS_ALREADY_DONE.getMsg());
        }

        if (status == EventStatus.FAIL) {
            throw new CustomException(OUTBOX_IS_ALREADY_FAIL,
                    OUTBOX_IS_ALREADY_FAIL.getMsg());
        }
        status = EventStatus.DONE;
    }


    public void plusRetryCount() {
        retryCount++;
    }

    public void fail() {
        if (status == EventStatus.DONE) {
            throw new CustomException(OUTBOX_IS_ALREADY_DONE,
                    OUTBOX_IS_ALREADY_DONE.getMsg());
        }

        status = EventStatus.FAIL;
    }

    public void restore() {
        status = EventStatus.INIT;
    }

    public enum EventType {
        PAYMENT,
    }

    public enum EventStatus {
        INIT,
        DONE,
        FAIL
    }
}
