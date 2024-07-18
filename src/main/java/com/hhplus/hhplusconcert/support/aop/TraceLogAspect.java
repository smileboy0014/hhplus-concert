package com.hhplus.hhplusconcert.support.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class TraceLogAspect {
    private static final String PREFIX = "[TRACE_LOG] ";


    private static final Logger logger = LoggerFactory.getLogger(TraceLogAspect.class);

    @Around("@within(com.hhplus.hhplusconcert.support.aop.TraceLog)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        long startTime = System.currentTimeMillis();

        logger.info(PREFIX + "Incoming request: [{} {}] from [{}]", request.getMethod(), request.getRequestURI(), request.getRemoteAddr());

        Object result;
        result = joinPoint.proceed();

        long elapsedTime = System.currentTimeMillis() - startTime;
        logger.info(PREFIX + "Outgoing response: [{} {}] took [{}] ms", request.getMethod(), request.getRequestURI(), elapsedTime);

        return result;
    }
}