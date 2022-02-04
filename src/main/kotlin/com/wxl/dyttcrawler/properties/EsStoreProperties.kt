package com.wxl.dyttcrawler.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

/**
 * Create by wuxingle on 2021/10/11
 * es相关配置
 */
@ConfigurationProperties(prefix = "crawler.store.es")
data class EsStoreProperties(

    @NestedConfigurationProperty
    var pool: EsPoolProperties = EsPoolProperties()
)

data class EsPoolProperties(

    var maxThreads: Int = 5
)

