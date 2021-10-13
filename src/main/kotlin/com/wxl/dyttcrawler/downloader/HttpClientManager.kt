package com.wxl.dyttcrawler.downloader

import org.apache.http.HttpRequestInterceptor
import org.apache.http.impl.client.*
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.impl.cookie.BasicClientCookie
import us.codecraft.webmagic.Site

/**
 * Create by wuxingle on 2021/10/08
 * httpClient管理
 */
class HttpClientManager(
    private val connectionManager: PoolingHttpClientConnectionManager
) {

    private val httpClientRegistries = mutableMapOf<String, CloseableHttpClient>()

    /**
     * 设置线程池大小
     */
    fun setPoolSize(poolSize: Int) {
        connectionManager.maxTotal = poolSize
    }

    /**
     * 优先从缓存获取client
     */
    fun getHttpClient(site: Site): CloseableHttpClient =
        httpClientRegistries.computeIfAbsent(site.domain) { createHttpClient(site) }

    /**
     * 创建http client
     */
    private fun createHttpClient(site: Site): CloseableHttpClient {
        val httpClientBuilder = HttpClients.custom().apply {
            setConnectionManager(connectionManager)
            if (site.userAgent != null) setUserAgent(site.userAgent) else setUserAgent("")
            if (site.isUseGzip) {
                addInterceptorFirst(HttpRequestInterceptor { req, _ ->
                    if (!req.containsHeader("Accept-Encoding")) {
                        req.addHeader("Accept-Encoding", "gzip")
                    }
                })
            }

            // dytt特殊的重定向策略
            setRedirectStrategy(DyttRedirectStrategy())
            // 重试设置
            setRetryHandler(DefaultHttpRequestRetryHandler(site.retryTimes, true))
            // cookie设置
            configCookieStore(this, site)
        }

        return httpClientBuilder.build()
    }

    private fun configCookieStore(httpClientBuilder: HttpClientBuilder, site: Site) = httpClientBuilder.apply {
        if (site.isDisableCookieManagement) {
            disableCookieManagement()
            return@apply
        }

        val cookieStore = BasicCookieStore().apply {
            for ((k, v) in site.cookies) {
                val cookie = BasicClientCookie(k, v).apply { domain = site.domain }
                addCookie(cookie)
            }

            for ((domainKey, domainVal) in site.allCookies) {
                for ((k, v) in domainVal) {
                    val cookie = BasicClientCookie(k, v).apply { domain = domainKey }
                    addCookie(cookie)
                }
            }
        }

        setDefaultCookieStore(cookieStore)
    }
}