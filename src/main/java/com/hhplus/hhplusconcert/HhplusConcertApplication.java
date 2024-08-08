package com.hhplus.hhplusconcert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching // 캐시 기능을 활성화한다.
public class HhplusConcertApplication {

    public static void main(String[] args) {
        SpringApplication.run(HhplusConcertApplication.class, args);
    }

}
