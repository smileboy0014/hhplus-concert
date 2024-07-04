package com.hhplus.hhplusconcert.presentation.common.dto;


import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class ApiResponse<T> {
    private final int code;
    private final HttpStatus status;
    private final String msg;
    private final T data;

    public ApiResponse(HttpStatus status, String msg, T data) {
        this.code = status.value();
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ApiResponse<T> of(HttpStatus httpStatus, String message, T data) {
        return new ApiResponse<>(httpStatus, message, data);
    }

    public static <T> ApiResponse<T> of(HttpStatus httpStatus, T data) {
        return new ApiResponse<>(httpStatus, httpStatus.name(), data);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return of(HttpStatus.OK, data);
    }


}
