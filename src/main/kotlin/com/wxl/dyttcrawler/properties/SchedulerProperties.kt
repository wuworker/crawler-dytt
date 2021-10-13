package com.wxl.dyttcrawler.properties

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Create by wuxingle on 2021/10/11
 * 任务队列管理
 */
@ConfigurationProperties(prefix = "crawler.scheduler")
data class SchedulerProperties(
    var type: Type = Type.LOCAL
) {

    enum class Type {
        LOCAL, REDIS
    }

}

