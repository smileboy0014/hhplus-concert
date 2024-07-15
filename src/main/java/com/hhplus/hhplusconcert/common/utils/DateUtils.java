package com.hhplus.hhplusconcert.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    /**
     * LocalDateTime 형식을 String 형식으로 변환한다.
     * @param localDateTime localDateTime 정보
     * @return String String 타입
     */
    public static String getLocalDateTimeToString(LocalDateTime localDateTime) {

        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
