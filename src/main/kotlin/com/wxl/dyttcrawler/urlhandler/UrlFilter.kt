package com.wxl.dyttcrawler.urlhandler

import java.net.URL

/**
 * Create by wuxingle on 2021/10/11
 * url过滤
 */
fun interface UrlFilter {

    /**
     * 是否匹配
     */
    fun match(url: URL): Boolean

    /**
     * 提取匹配的url
     */
    fun filter(urls: List<URL>): List<URL> {
        return urls.filter { match(it) }
    }

}
