package com.wxl.dyttcrawler.downloader

import com.wxl.dyttcrawler.core.CrawlerListener
import org.apache.http.HttpResponse
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import us.codecraft.webmagic.Page
import us.codecraft.webmagic.Request
import us.codecraft.webmagic.Task
import us.codecraft.webmagic.downloader.HttpUriRequestConverter
import us.codecraft.webmagic.proxy.ProxyProvider
import us.codecraft.webmagic.selector.PlainText
import us.codecraft.webmagic.utils.CharsetUtils
import us.codecraft.webmagic.utils.HttpClientUtils
import java.io.IOException
import java.nio.charset.Charset

/**
 * Create by wuxingle on 2021/9/25
 * httpClient下载
 */
open class HttpClientDownloader(
    private val httpClient: CloseableHttpClient,
    private val defaultCharset: String,
    private val proxyProvider: ProxyProvider? = null,
    private val httpUriRequestConverter: HttpUriRequestConverter = HttpUriRequestConverter()
) : HttpDownloader {

    private val crawlerListeners = mutableListOf<CrawlerListener>()

    var responseHeader = false

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun download(request: Request, task: Task): Page {
        val proxy = proxyProvider?.getProxy(task)
        val requestCtx = httpUriRequestConverter.convert(request, task.site, proxy)
        var page = Page.fail()
        var response: CloseableHttpResponse? = null
        try {
            response = httpClient.execute(requestCtx.httpUriRequest, requestCtx.httpClientContext)
            page = handleResponse(request, response, request.charset ?: task.site.charset)
            // 成功通知
            onSuccess(request, task)
            log.info("downloading page success {}", request.url)
            return page
        } catch (e: IOException) {
            log.warn("download page {} error", request.url, e)
            // 失败通知
            onError(request, task)
            return page
        } finally {
            if (response != null) {
                //ensure the connection is released back to pool
                EntityUtils.consumeQuietly(response.entity)
            }
            if (proxyProvider != null && proxy != null) {
                proxyProvider.returnProxy(proxy, page, task)
            }
        }
    }

    /**
     * 响应处理
     */
    @Throws(IOException::class)
    private fun handleResponse(request: Request, response: HttpResponse, charset: String?): Page {
        val bytes = EntityUtils.toByteArray(response.entity)
        val contentType = response.entity?.contentType?.value ?: ""
        return Page().apply {
            this.bytes = bytes
            if (!request.isBinaryContent) {
                this.charset = charset ?: getHtmlCharset(contentType, bytes)
                rawText = String(bytes, Charset.forName(this.charset))
            }
            url = PlainText(request.url)
            this.request = request
            statusCode = response.statusLine.statusCode
            isDownloadSuccess = true
            if (responseHeader) {
                headers = HttpClientUtils.convertHeaders(response.allHeaders)
            }
        }
    }

    protected fun onSuccess(request: Request, task: Task) {
        for (crawlerListener in crawlerListeners) {
            try {
                crawlerListener.onSuccess(request, task)
            } catch (e: Exception) {
                log.error("download on success process error:{}", request, e)
            }
        }
    }

    protected fun onError(request: Request, task: Task) {
        for (crawlerListener in crawlerListeners) {
            try {
                crawlerListener.onError(request, task)
            } catch (e: Exception) {
                log.error("download on error process error:{}", request, e)
            }
        }
    }

    /**
     * 添加监听
     */
    fun addCrawlerListeners(vararg listeners: CrawlerListener) = crawlerListeners.addAll(listeners)

    private fun getHtmlCharset(contentType: String, contentBytes: ByteArray): String {
        var charset = CharsetUtils.detectCharset(contentType, contentBytes)
        if (charset == null) {
            charset = defaultCharset
            log.warn(
                "Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()",
                defaultCharset
            )
        }
        return charset
    }
}
