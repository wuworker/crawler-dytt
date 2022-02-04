package com.wxl.dyttcrawler.downloader

import org.apache.http.HttpHeaders
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.impl.client.LaxRedirectStrategy
import org.apache.http.protocol.HttpContext
import org.slf4j.LoggerFactory
import java.net.URI

/**
 * Create by wuxingle on 2021/10/08
 * 电影天堂的重定向策略
 * 200也可能是重定向。
 */
class DyttRedirectStrategy : LaxRedirectStrategy() {

    companion object {
        private val log = LoggerFactory.getLogger(DyttRedirectStrategy::class.java)
    }

    override fun isRedirected(request: HttpRequest, response: HttpResponse, context: HttpContext): Boolean {
        val isMoved = super.isRedirected(request, response, context)
        if (!isMoved) {
            val method = request.requestLine.method
            if (isRedirectable(method)) {
                val locationHeader = response.getFirstHeader(HttpHeaders.CONTENT_LOCATION)
                if (locationHeader != null) {
                    log.debug(
                        "{},has content-location header:{}",
                        request.requestLine.uri, locationHeader.value
                    )
                    response.addHeader("location", locationHeader.value)
                    return true
                }
            }
            return false
        }
        return true
    }

    override fun getLocationURI(request: HttpRequest, response: HttpResponse, context: HttpContext): URI {
        return super.getLocationURI(request, response, context)
    }
}