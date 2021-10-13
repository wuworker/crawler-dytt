package com.wxl.dyttcrawler.urlhandler

import com.wxl.dyttcrawler.core.DyttPattern
import org.springframework.stereotype.Component
import java.net.URL

/**
 * Create by wuxingle on 2021/10/12
 * url匹配
 * 协议，域名必须匹配
 */
@Component
class DyttUrlFilter : UrlFilter {

    /**
     * url协议和域名必须匹配
     */
    override fun match(url: URL): Boolean =
        DyttPattern.PROTOCOL_PATTERN.matcher(url.protocol).matches()
                && DyttPattern.DOMAIN_PATTERN.matcher(url.host).matches()

}