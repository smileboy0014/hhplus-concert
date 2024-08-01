package com.hhplus.hhplusconcert.support.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    /**
     * Lock 이름
     */
    String key();

    /**
     * Lock 시간 단위
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * Lock 기다리는 시간 (default - 5s)
     * 락 획득을 위해 waitTime 만큼 대기한다
     */
    long waitTime() default 5L;

    /**
     * Lock 임대 시간 (default - 3s)
     * 락을 획득한 이후 leaseTime 이 지나면 락을 해제한다
     */
    long leaseTime() default 3L;
}
