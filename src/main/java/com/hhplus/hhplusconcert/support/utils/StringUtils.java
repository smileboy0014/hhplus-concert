package com.hhplus.hhplusconcert.support.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {

    /**
     * 기간 정보로 반환한다.
     *
     * @param s1 : 기간 시작일
     * @param s2 : 기간 마감일
     * @return String 기간 정보
     */
    public static String getPeriod(String s1, String s2) {

        return String.join("~", s1, s2);
    }
}