package com.wxl.dyttcrawler.properties

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Create by wuxingle on 2021/10/02
 * 爬虫配置
 */
@ConfigurationProperties(prefix = "crawler")
data class CrawlerProperties(

    var taskId: String? = null,

    var startUrl: String = "https://www.dytt8.net",

    var allowDomains: List<String> = emptyList(),

    var maxThreads: Int = 5,
)

