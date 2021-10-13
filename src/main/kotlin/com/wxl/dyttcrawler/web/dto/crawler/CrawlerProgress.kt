package com.wxl.dyttcrawler.web.dto.crawler

/**
 * Create by wuxingle on 2021/10/13
 * 爬虫进度
 */
data class CrawlerProgress(
    /**
     * 待处理数
     */
    var todoSize: Int? = null,

    /**
     * 处理总数
     */
    var totalSize: Int? = null,

    /**
     * 处理失败数
     */
    var failSize: Int? = null
)

