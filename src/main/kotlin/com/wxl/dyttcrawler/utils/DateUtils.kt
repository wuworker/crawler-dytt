package com.wxl.dyttcrawler.utils

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

/**
 * Create by wuxingle on 2021/10/11
 * 日期工具类
 */
class DateUtils {

    /**
     * 解析为Date类型
     */
    companion object {

        fun parseDate(dateStr: String, formatter: DateTimeFormatter): Date {
            val dateTime = LocalDate.parse(dateStr, formatter).atStartOfDay(ZoneId.systemDefault())
            return Date.from(dateTime.toInstant())
        }

        fun parseDate(dateStr: String, vararg formatters: DateTimeFormatter): Date? {
            for (formatter in formatters) {
                try {
                    val dateTime = LocalDate.parse(dateStr, formatter).atStartOfDay(ZoneId.systemDefault())
                    return Date.from(dateTime.toInstant())
                } catch (e: DateTimeParseException) {
                    //ignore
                }
            }
            return null
        }
    }


}
