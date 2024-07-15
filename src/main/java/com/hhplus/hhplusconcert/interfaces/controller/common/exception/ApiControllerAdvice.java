package com.hhplus.hhplusconcert.interfaces.controller.common.exception;

import com.hhplus.hhplusconcert.domain.common.exception.CustomBadRequestException;
import com.hhplus.hhplusconcert.domain.common.exception.CustomNotFoundException;
import com.hhplus.hhplusconcert.interfaces.controller.common.dto.ApiResultResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    @ApiResponse(responseCode = "400", description = "Bad Request")

    public ApiResultResponse<Object> bindException(BindException e) {
        return ApiResultResponse.of(HttpStatus.BAD_REQUEST,
                e.getBindingResult().getAllErrors().get(0).getDefaultMessage(), null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomBadRequestException.class)
    @ApiResponse(responseCode = "400", description = "Bad Request")
    public ApiResultResponse<Object> handleCustomException(CustomBadRequestException e) {
        return ApiResultResponse.of(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomNotFoundException.class)
    @ApiResponse(responseCode = "404", description = "Not Found")
    public ApiResultResponse<Object> handleCustomException(CustomNotFoundException e) {
        return ApiResultResponse.of(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }


}
