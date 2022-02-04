package com.wxl.dyttcrawler.properties

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Create by wuxingle on 2021/10/02
 * 爬虫配置
 */
@ConfigurationProperties(prefix = "crawler")
data class CrawlerProperties(

    /**
     * 任务id
     */
    var taskId: String? = null,

    /**
     * 拉取起始地址
     */
    var startUrl: String = "https://www.dytt8.net",

    /**
     * 匹配的域名
     */
    var allowDomains: List<String> = emptyList(),

    /**
     * 爬虫任务底层线程数
     */
    var maxThreads: Int = 5,

    /**
     * 爬虫任务并发度
     */
    var concurrentNum: Int = 1
)

