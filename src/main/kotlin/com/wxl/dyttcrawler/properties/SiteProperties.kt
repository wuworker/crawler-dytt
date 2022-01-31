package com.wxl.dyttcrawler.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * Create by wuxingle on 2022/01/08
 * 爬虫站点配置
 */
@ConfigurationProperties(prefix = "crawler.site")
data class SiteProperties(
    var domain: String = "dytt",
    var charset: String = "gbk",
    var userAgent: String? = null,
    var sleepTime: Duration = Duration.ofSeconds(1),
    //httpclient重试
    var retryTimes: Int = 0,
    // 爬虫重试次数
    var cycleRetryTimes: Int = 1,
    var retrySleepTime: Duration = Duration.ofSeconds(1),
    var timeout: Duration = Duration.ofSeconds(10),
    var acceptStatusCode: List<Int> = listOf(200),
    var headers: Map<String, String> = emptyMap(),
    var useGzip: Boolean = true,
)

