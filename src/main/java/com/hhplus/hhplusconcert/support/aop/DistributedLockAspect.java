package com.hhplus.hhplusconcert.support.aop;

import com.hhplus.hhplusconcert.domain.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static com.hhplus.hhplusconcert.domain.common.exception.ErrorCode.LOCK_ACQUIRE_FAILED;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE) //Transactional 어노테이션 보다 우선 순위를 높이기 위해
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final RedissonClient redissonClient;

    private static final String REDISSON_KEY_PREFIX = "RLOCK_";

    @Around("@annotation(com.hhplus.hhplusconcert.support.aop.DistributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = REDISSON_KEY_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());
        RLock lock = redissonClient.getLock(key);

        try {
            // 락 획득 시도
            boolean available = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            if (!available) {
                throw new CustomException(LOCK_ACQUIRE_FAILED, LOCK_ACQUIRE_FAILED.getMsg());
            }
            log.info("Redisson GetLock {}", key);
            return joinPoint.proceed();
        } finally {
            lock.unlock();
        }
    }
}
