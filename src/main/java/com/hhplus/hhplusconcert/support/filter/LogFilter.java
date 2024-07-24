package com.hhplus.hhplusconcert.support.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LogFilter implements Filter {

    private static final String PREFIX = "[LOG_FILTER] ";
    private static final Logger logger = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info(PREFIX + "LogFilter init()");
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest((HttpServletRequest) request);
        CachedBodyHttpServletResponse cachedResponse = new CachedBodyHttpServletResponse((HttpServletResponse) response);

        String requestURI = cachedRequest.getRequestURI();
        String httpMethod = cachedRequest.getMethod();
        String queryString = cachedRequest.getQueryString();
        String threadName = Thread.currentThread().getName();
        String requestBody = cachedRequest.getCachedBody();

        logger.info(PREFIX + "Request URI: {}, HTTP Method: {}, Query Parameters: {}, Thread: {}, Request Body: {}",
                requestURI, httpMethod, queryString, threadName, requestBody);

        try {
            chain.doFilter(cachedRequest, cachedResponse);
        } finally {
            cachedResponse.flushBuffer();  // Ensure cached content is flushed to the original response
            String responseBody = cachedResponse.getCachedBody();
            int statusCode = cachedResponse.getStatus();
            logger.info(PREFIX + "Response Code: {}, Thread: {}, Response Body: {}, LoggingFilter doFilter End",
                    statusCode, threadName, responseBody);
        }
    }

    @Override
    public void destroy() {
        logger.info(PREFIX + "LogFilter destroy()");
    }
}