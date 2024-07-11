package com.hhplus.hhplusconcert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HhplusConcertApplication {

    public static void main(String[] args) {
        SpringApplication.run(HhplusConcertApplication.class, args);
    }

}
