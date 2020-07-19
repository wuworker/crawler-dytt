package com.wxl.dyttcrawler.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * Create by wuxingle on 2020/7/18
 * 日期工具类
 */
public class DateUtils {

    /**
     * 解析为Date类型
     */
    public static Date parseDate(String dateStr, DateTimeFormatter formatter)
            throws DateTimeParseException {
        ZonedDateTime dateTime = LocalDate.parse(dateStr, formatter).atStartOfDay(ZoneId.systemDefault());
        return Date.from(dateTime.toInstant());
    }


}
