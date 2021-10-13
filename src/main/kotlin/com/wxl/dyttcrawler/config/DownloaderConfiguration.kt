package com.wxl.dyttcrawler.config

import com.wxl.dyttcrawler.core.CrawlerListener
import com.wxl.dyttcrawler.downloader.HttpClientDownloader
import com.wxl.dyttcrawler.downloader.HttpClientManager
import com.wxl.dyttcrawler.properties.CrawlerProperties
import com.wxl.dyttcrawler.properties.HttpDownloadProperties
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.socket.PlainConnectionSocketFactory
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.TrustAllStrategy
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.ssl.SSLContextBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import us.codecraft.webmagic.proxy.ProxyProvider
import us.codecraft.webmagic.proxy.SimpleProxyProvider
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket

/**
 * Create by wuxingle on 2021/9/25
 * http下载配置
 */
@Configuration
class DownloaderConfiguration(
    private val httpDownloadProperties: HttpDownloadProperties,
    private val crawlerProperties: CrawlerProperties
) {

    companion object {
        private val log = LoggerFactory.getLogger(DownloaderConfiguration::class.java)
    }

    /**
     * http下载器
     */
    @Bean
    fun httpClientDownloader(
        httpClientManager: HttpClientManager,
        crawlerListeners: ObjectProvider<CrawlerListener>
    ): HttpClientDownloader {
        var proxyProvider: ProxyProvider? = null
        if (httpDownloadProperties.proxies.isNotEmpty()) {
            proxyProvider = SimpleProxyProvider(httpDownloadProperties.proxies)
        }

        val downloader = HttpClientDownloader(httpClientManager, crawlerProperties.charset, proxyProvider)

        val listeners = crawlerListeners.orderedStream().collect(Collectors.toList())
        downloader.addCrawlerListeners(*listeners.toTypedArray())
        return downloader
    }

    /**
     * httpClient管理
     */
    @Bean
    fun httpClientManager(poolManager: PoolingHttpClientConnectionManager): HttpClientManager =
        HttpClientManager(poolManager)

    /**
     * http连接池
     */
    @Bean
    fun poolingHttpClientConnectionManager(): PoolingHttpClientConnectionManager {
        val registry = RegistryBuilder.create<ConnectionSocketFactory>().run {
            register("http", PlainConnectionSocketFactory.INSTANCE)
            register("https", sslConnectionSocketFactory())
            build()
        }

        val poolConfig = httpDownloadProperties.pool

        return PoolingHttpClientConnectionManager(
            registry, null, null, null,
            poolConfig.keepAlive.seconds, TimeUnit.SECONDS
        ).apply {
            maxTotal = poolConfig.maxThreads
            defaultMaxPerRoute = poolConfig.maxThreads
            validateAfterInactivity = poolConfig.validateAfterInactivity.toMillis().toInt()

            //todo socket config connect config
        }
    }

    /**
     * 获取SSLSocketFactory
     */
    private fun sslConnectionSocketFactory(): SSLConnectionSocketFactory {
        val ignoreSSL = httpDownloadProperties.ignoreSsl
        val useSecurity = httpDownloadProperties.useSecurity
        if (ignoreSSL) {
            val sslContext: SSLContext = SSLContextBuilder().run {
                loadTrustMaterial(TrustAllStrategy.INSTANCE)
                build()
            }

            var protocols: Array<String>? = null
            var cipherSuites: Array<String>? = null

            // 允许使用不安全的协议和加密套件
            if (!useSecurity) {
                protocols = getEnabledProtocols(sslContext)
                cipherSuites = getEnabledCipherSuites(sslContext)
                if (log.isDebugEnabled) {
                    log.debug("https use protocols:{}", Arrays.toString(protocols))
                    log.debug("https use cipher suites:{}", Arrays.toString(cipherSuites))
                }
            }

            return SSLConnectionSocketFactory(sslContext, protocols, cipherSuites, NoopHostnameVerifier.INSTANCE)
        } else {
            return SSLConnectionSocketFactory.getSocketFactory()
        }
    }

    /**
     * 获取https协议TLSv1,TLSv1.1,TLSv1.2
     * 可能包含不安全的协议
     */
    private fun getEnabledProtocols(sslContext: SSLContext): Array<String> {
        var sslSocket: SSLSocket? = null
        try {
            sslSocket = sslContext.socketFactory.createSocket() as SSLSocket
            return sslSocket.enabledProtocols
        } finally {
            sslSocket?.close()
        }
    }

    /**
     * 获取https加密套件，包含不安全的加密算法
     */
    private fun getEnabledCipherSuites(sslContext: SSLContext): Array<String> {
        var sslSocket: SSLSocket? = null
        try {
            sslSocket = sslContext.socketFactory.createSocket() as SSLSocket
            return sslSocket.enabledCipherSuites
        } finally {
            sslSocket?.close()
        }
    }

}