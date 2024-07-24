package com.hhplus.hhplusconcert.support.interceptor;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.support.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.TOKEN_IS_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Profile("!test") //테스트 환경에 영향을 주기 떄문에 설정
public class TokenInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public boolean preHandle(
            HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {

        // favicon.ico 요청에 대한 JWT 토큰 검증을 건너뛰기
        if (request.getRequestURI().equals("/favicon.ico")) {
            return true;
        }

        //web 과의 연동으로 인한 CORS 정책 판단 조건
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        // 인가 요청 여부 확인
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasLength(authorization)) {
            throw new CustomException(TOKEN_IS_NOT_FOUND,
                    "토큰이 존재하지 않습니다. 다시 토큰을 헤더에 싣고 시도해주세요.");
        }
        // JWT 여부 확인
        String token = authorization.replaceAll("Bearer ", "");

        // token의 값이 존재하는지 확인
        Long userId = jwtUtils.resolveToken(token);
        // 토큰이 유효한 토큰인지 검증
        jwtUtils.validToken(userId, token);
        return true;
    }
}
