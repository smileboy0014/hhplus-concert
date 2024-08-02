package com.hhplus.hhplusconcert.support.utils;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import com.hhplus.hhplusconcert.domain.common.exception.ErrorCode;
import com.hhplus.hhplusconcert.domain.queue.WaitingQueueRepository;
import com.hhplus.hhplusconcert.domain.user.User;
import com.hhplus.hhplusconcert.domain.user.UserRepository;
import com.hhplus.hhplusconcert.infrastructure.redis.RedisRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.*;
import static com.hhplus.hhplusconcert.domain.queue.WaitingQueueConstants.ACTIVE_KEY;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret-key}")
    public String SECRET_KEY;

    // 토큰 유효시간 1시간
    public static final long EXP_TIME = 60 * 60 * 1000L;

    private final WaitingQueueRepository waitingQueueRepository;
    private final UserRepository userRepository;
    private final RedisRepository redisRepository;

    /**
     * 토큰을 생성한다.
     *
     * @param userId userId 정보
     * @return String token 정보
     */
    public String createToken(Long userId) {

        return Jwts
                .builder()
                .setSubject(String.valueOf(userId)) // JWT payload 에 저장되는 정보
                .setIssuedAt(new Date(System.currentTimeMillis())) //발행 일자
                .setExpiration(new Date(System.currentTimeMillis() + EXP_TIME)) // set Expire Time
                .signWith(this.getSigningKey())  // 사용할 암호화 알고리즘과 secret key 세팅
                .compact();
    }

    /**
     * JWT 토큰 정보를 해독한다.
     *
     * @param token JWT token 정보
     * @return Long userId 정보
     */
    @Transactional(noRollbackFor = CustomException.class)
    public Long resolveToken(String token) {
        // 토큰 추출
        if (!StringUtils.hasText(token)) {
            throw new CustomException(TOKEN_IS_NOT_FOUND,
                    "토큰이 존재하지 않습니다. 다시 토큰을 발급 후 시도해주세요.");
        }
        // 토큰 파싱
        Jws<Claims> claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(this.getSigningKey())
                    .build()
                    .parseClaimsJws(token);

        } catch (ExpiredJwtException e) {
            changeTokenStatus(token);
            log.error("토큰이 만료되었습니다. {}", e.getMessage());
            throw new CustomException(TOKEN_IS_EXPIRED, "토큰이 만료되었습니다, 재빌급 받아주세요.");
        } catch (Exception e) {
            log.error("토큰 파싱 중 오류가 발생하였습니다. {}", e.getMessage());
            throw new CustomException(INVALID_TOKEN, "토큰이 유효하지 않습니다, 토큰을 재발급 받으세요.");
        }

        log.debug("claims.getBody : {}", claims.getBody());
        log.debug("claims.getHeader : {}", claims.getHeader());

        // userId 추출
        String subject = claims.getBody().getSubject();
        try {
            return Long.valueOf(subject);
        } catch (NumberFormatException e) {
            log.error("userId 변환 중 오류가 발생히였습니다. {}", e.getMessage());
            throw new CustomException(INVALID_TOKEN_PAYLOAD, "USER ID 형식이 올바르지 않습니다.");
        }
    }

    /**
     * db에 있는 토큰 상태를 만료로 변경한다.
     *
     * @param token JWT 토큰 정보
     */
    private void changeTokenStatus(String token) {
        waitingQueueRepository.deleteExpiredToken(token);
    }

    /**
     * WaitingQueue 에 있는 토큰 정보가 유효한지 확인한다.
     *
     * @param userId userId 정보
     * @param token token 정보
     */
    public void validToken(Long userId, String token) {
        // 사용자가 존재하는지 확인
        Optional<User> user = userRepository.getUser(userId);

        if (user.isEmpty()) {
            throw new CustomException(ErrorCode.USER_IS_NOT_FOUND,
                    "토큰을 다시 발급 받아 주세요.");
        }
        Long tokenRank = redisRepository.zSetRank(ACTIVE_KEY, token);
        if (tokenRank == null) {
            throw new CustomException(ErrorCode.TOKEN_IS_NOT_FOUND,
                    "활성화 된 토큰이 아닙니다. 다시 토큰을 발급 받거나 대기해주세요.");
        }

    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
