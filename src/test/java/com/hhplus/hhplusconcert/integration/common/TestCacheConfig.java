package com.hhplus.hhplusconcert.integration.common;

import com.hhplus.hhplusconcert.domain.common.config.CacheConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import(CacheConfig.class)
public class TestCacheConfig {

    @Bean
    public CacheConfig cacheConfig() {
        return new CacheConfig();
    }
}