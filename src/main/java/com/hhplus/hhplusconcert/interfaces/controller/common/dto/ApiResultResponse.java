package com.hhplus.hhplusconcert.interfaces.controller.common.dto;


import org.springframework.http.HttpStatus;

public record ApiResultResponse<T>(HttpStatus status, String msg, T data) {

    public static <T> ApiResultResponse<T> of(HttpStatus httpStatus, String message, T data) {
        return new ApiResultResponse<>(httpStatus, message, data);
    }

    public static <T> ApiResultResponse<T> of(HttpStatus httpStatus, T data) {
        return new ApiResultResponse<>(httpStatus, httpStatus.name(), data);
    }


    public static <T> ApiResultResponse<T> ok(T data) {
        return of(HttpStatus.OK, data);
    }


}
