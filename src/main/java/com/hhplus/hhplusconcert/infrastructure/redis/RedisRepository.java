package com.hhplus.hhplusconcert.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String REDIS_NAMESPACE = "hhplus:";
    private final ObjectMapper objectMapper = new ObjectMapper();

    // sorted_set 추가
    public Boolean zSetAdd(String key, String value, double score) {
        return redisTemplate.opsForZSet().addIfAbsent(REDIS_NAMESPACE + key, value, score);
    }

    // sorted_set 추가
    public void zSetAddRange(String key, Set<String> value) {
        value.forEach(token -> redisTemplate.opsForZSet().add(REDIS_NAMESPACE + key, token, System.currentTimeMillis()));
    }

    // sorted_set 삭제
    public void zSetRemove(String key, String value) {
        redisTemplate.opsForZSet().remove(REDIS_NAMESPACE + key, value);
    }

    //sorted_set 순서 구하기
    public Long zSetRank(String key, String token) {
        return redisTemplate.opsForZSet().rank(REDIS_NAMESPACE + key, token);
    }

    public Long zSetSize(String key) {
        return redisTemplate.opsForZSet().size(REDIS_NAMESPACE + key);
    }


    public boolean zSetElementExist(String activeKey, String token) {
        Double score = redisTemplate.opsForZSet().score(activeKey, token);
        return score != null;
    }

    public Set<String> zSetGetRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(REDIS_NAMESPACE + key, start, end);
    }

    public Set<String> zSetGetRangeByScore(String activeKey, int start, long end) {
        return redisTemplate.opsForZSet().rangeByScore(REDIS_NAMESPACE + activeKey, start, end);
    }

    public void zSetRemoveRange(String key, int start, int end) {
        redisTemplate.opsForZSet().removeRange(REDIS_NAMESPACE + key, start, end);
    }

    public void zSetRemoveRangeByScore(String key, long start, long end) {
        redisTemplate.opsForZSet().removeRangeByScore(REDIS_NAMESPACE + key, start, end);
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

}
