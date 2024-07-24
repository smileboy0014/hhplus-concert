package com.hhplus.hhplusconcert.support.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ErrorHandler;

@Slf4j
public class CustomErrorHandler implements ErrorHandler {

    @Override
    public void handleError(Throwable t) {
        log.info(t.getMessage());
    }
}