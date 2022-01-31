package com.wxl.dyttcrawler.urlhandler

import com.wxl.dyttcrawler.properties.CrawlerProperties
import org.springframework.stereotype.Component
import java.net.URL
import java.util.regex.Pattern

/**
 * Create by wuxingle on 2021/10/12
 * url匹配
 * 协议，域名必须匹配
 */
@Component
class DyttUrlFilter(
    private val crawlerProperties: CrawlerProperties
) : UrlFilter {
    /**
     * 爬取协议正则
     */
    private val protocolPattern: Pattern = Pattern.compile("http|https")

    /**
     * 允许访问的域名
     */
    private var allowDomains: Set<String> = crawlerProperties.allowDomains.toSet()

    /**
     * url协议和域名必须匹配
     */
    override fun match(url: URL): Boolean =
        protocolPattern.matcher(url.protocol).matches()
                && allowDomains.contains(url.host)

}