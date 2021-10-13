package com.wxl.dyttcrawler.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import java.time.Duration

/**
 * Create by wuxingle on 2021/10/02
 * 爬虫配置
 */
@ConfigurationProperties(prefix = "crawler")
data class CrawlerProperties(

    var taskId: String? = null,

    var firstUrl: String = "https://www.dytt8.net",

    var charset: String = "gbk",

    var maxThreads: Int = 5,

    @NestedConfigurationProperty
    var site: SiteProperties = SiteProperties(),
)

data class SiteProperties(
    var userAgent: String? = null,
    var sleepTime: Duration = Duration.ofSeconds(1),
    var retryTimes: Int = 1,
    var retrySleepTime: Duration = Duration.ofSeconds(1),
    var timeout: Duration = Duration.ofSeconds(10),
    var acceptStatusCode: List<Int> = listOf(200),
    var headers: Map<String, String> = emptyMap(),
    var disableCookie: Boolean = true
)

