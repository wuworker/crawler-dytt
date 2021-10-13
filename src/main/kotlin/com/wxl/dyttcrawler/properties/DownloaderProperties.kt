package com.wxl.dyttcrawler.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import us.codecraft.webmagic.proxy.Proxy
import java.time.Duration

/**
 * Create by wuxingle on 2021/10/02
 * 下载配置
 */
@ConfigurationProperties(prefix = "crawler.download")
data class HttpDownloadProperties(

    /**
     * 忽略https证书校验
     */
    var ignoreSsl: Boolean = true,

    /**
     * 使用安全的https协议和加密套件
     */
    var useSecurity: Boolean = false,

    /**
     * 连接池
     */
    @NestedConfigurationProperty
    var pool: HttpPoolProperties = HttpPoolProperties(),

    /**
     * 代理配置
     */
    @NestedConfigurationProperty
    var proxies: List<Proxy> = emptyList()
)

data class HttpPoolProperties(
    /**
     * 最大连接数
     */
    var maxThreads: Int = 10,

    /**
     * http连接的keepAlive
     */
    var keepAlive: Duration = Duration.ofMinutes(3),

    /**
     * 空闲n时间后需要校验
     */
    var validateAfterInactivity: Duration = Duration.ofMinutes(1)
)



