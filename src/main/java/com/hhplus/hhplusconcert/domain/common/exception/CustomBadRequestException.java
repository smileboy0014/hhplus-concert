package com.hhplus.hhplusconcert.domain.common.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomBadRequestException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String msg;

    @Override
    public String getMessage() {
        return "[%s] %s".formatted(errorCode, msg);
    }
}