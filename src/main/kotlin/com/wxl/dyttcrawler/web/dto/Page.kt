package com.wxl.dyttcrawler.web.dto

/**
 * Create by wuxingle on 2021/10/13
 * 分页结果
 */
data class Page<T>(
    var list: List<T> = emptyList(),
    var total: Long = 0L
)
