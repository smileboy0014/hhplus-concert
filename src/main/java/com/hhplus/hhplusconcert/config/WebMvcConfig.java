package com.hhplus.hhplusconcert.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Profile("!test") //테스트 환경에 영향을 주기 떄문에 설정
public class WebMvcConfig implements WebMvcConfigurer {

    // JWT 인터셉터
    private final JwtTokenInterceptor jwtTokenInterceptor;

    // 인터셉터에 포함할 경로
    private final List<String> addEndPointList = List.of(
            "/**"
    );

    // 인터셉터에 포함하지 않을 경로
    private final List<String> excludePointList = List.of(
            "/**/swagger-resources/**",
            "/**/swagger-ui/**",
            "/**/v3/api-docs",
            "/**/api-docs/**",
            "/**/v1/users/**",
            "/**/v1/queues/**"
    );

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(jwtTokenInterceptor) // 어떤 인터셉터를 태울지
                .addPathPatterns(addEndPointList) // 인터셉터에 적용할 url
                .excludePathPatterns(excludePointList); // 인터셉터에서 제외할 url
    }
}