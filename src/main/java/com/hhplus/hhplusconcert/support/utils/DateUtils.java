package com.hhplus.hhplusconcert.support.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

    /**
     * LocalDateTime 형식을 String 형식으로 변환한다.
     *
     * @param localDateTime localDateTime 정보
     * @return String String 타입
     */
    public static String getLocalDateTimeToString(LocalDateTime localDateTime) {

        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
