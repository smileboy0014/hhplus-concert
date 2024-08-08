package com.hhplus.hhplusconcert.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String REDIS_NAMESPACE = "hhplus:";
    private static final String ACTIVE_COUNT_KEY = "hhplus:waiting:active:*";

    // sorted_set 추가
    public Boolean zSetAdd(String key, String value, double score) {
        return redisTemplate.opsForZSet().addIfAbsent(REDIS_NAMESPACE + key, value, score);
    }

    // sorted_set 삭제
    public void zSetRemove(String key, String value) {
        redisTemplate.opsForZSet().remove(REDIS_NAMESPACE + key, value);
    }

    //sorted_set 순서 구하기
    public Long zSetRank(String key, String token) {
        return redisTemplate.opsForZSet().rank(REDIS_NAMESPACE + key, token);
    }

    public Set<String> zSetGetRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(REDIS_NAMESPACE + key, start, end);
    }

    public void zSetRemoveRange(String key, int start, int end) {
        redisTemplate.opsForZSet().removeRange(REDIS_NAMESPACE + key, start, end);
    }

    //set 사용
    public Long setAdd(String key, String value) {
        return redisTemplate.opsForSet().add(REDIS_NAMESPACE + key, value);
    }

    public void setAddRangeWithTtl(String key, Set<String> value, long timeout, TimeUnit unit) {
        value.forEach(token -> {
            String[] tokenInfo = token.split(":");
            redisTemplate.opsForSet().add(REDIS_NAMESPACE + key + ":" + tokenInfo[0], tokenInfo[1]);
            setTtl(key + ":" + tokenInfo[0], timeout, unit);
        });
    }

    public Boolean setIsMember(String key, String value) {

        return redisTemplate.opsForSet().isMember(REDIS_NAMESPACE + key, value);
    }

    public void setTtl(String key, Long timeout, TimeUnit timeUnit) {
        redisTemplate.expire(REDIS_NAMESPACE + key, timeout, timeUnit);
    }

    public void clearCurrentDatabase() {
        RedisConnection connection = null;
        try {
            connection = RedisConnectionUtils.getConnection(Objects.requireNonNull(redisTemplate.getConnectionFactory()));
            connection.serverCommands().flushDb();  // Execute FLUSHDB command
        } finally {
            if (connection != null) {
                RedisConnectionUtils.releaseConnection(connection, redisTemplate.getConnectionFactory());
            }
        }
    }

    public Long countActiveTokens() {

        String luaScript = "local count = 0 " +
                "for _, key in ipairs(redis.call('keys', ARGV[1])) do " +
                "count = count + 1 " +
                "end " +
                "return count";

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript);
        redisScript.setResultType(Long.class);

        return redisTemplate.execute(redisScript, Collections.emptyList(), ACTIVE_COUNT_KEY);
    }


    public void deleteKey(String key) {
        redisTemplate.delete(REDIS_NAMESPACE + key);
    }

}
