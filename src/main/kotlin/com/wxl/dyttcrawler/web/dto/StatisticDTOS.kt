package com.wxl.dyttcrawler.web.dto

/**
 * Create by wuxingle on 2021/10/13
 * 统计相关DTO
 */

/**
 * 统计维度
 */
object StatDimension {
    /**
     * 种类
     */
    const val CATEGORY = "category"

    /**
     * 产地
     */
    const val PLACE = "originPlace"

    /**
     * 语言
     */
    const val LANGUAGE = "language"

    /**
     * 年代
     */
    const val YEAR = "year"
}

/**
 * 各维度数量
 */
data class BaseStatCount(
    /**
     * 总数
     */
    var count: Long? = null,
    /**
     * 种类
     */
    var category: Long? = null,
    /**
     * 产地
     */
    var place: Long? = null,
    /**
     * 语言
     */
    var language: Long? = null
)

/**
 * 月份数统计
 */
data class MonthCount(
    var key: Int? = null,
    var value: Long? = null
)

/**
 * 地区统计数据
 */
data class PlaceCount(
    var key: String? = null,
    var value: Long? = null
)

/**
 * 年-月份数统计
 */
data class YearMonthCount(
    var key: Int? = null,
    var value: List<MonthCount>? = null
)

/**
 * 年份地区数据
 */
data class YearPlaceCount(
    var key: String? = null,
    var value: List<PlaceCount>? = null
)

/**
 * term结果
 */
data class TermItem<K, V>(
    var items: List<TermEntry<K, V>> = emptyList(),
    var otherSize: Long = 0
)

data class TermEntry<K, V>(
    var key: K,
    var value: V
)