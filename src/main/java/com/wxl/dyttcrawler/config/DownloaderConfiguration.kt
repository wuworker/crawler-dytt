package com.wxl.dyttcrawler.config

import com.wxl.dyttcrawler.downloader.HttpClientDownloader
import com.wxl.dyttcrawler.properties.CrawlerProperties
import com.wxl.dyttcrawler.properties.DownloadProperties
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.TrustAllStrategy
import org.apache.http.ssl.SSLContextBuilder
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket

/**
 * Create by wuxingle on 2021/9/25
 * http下载配置
 */
@Configuration
@EnableConfigurationProperties(DownloadProperties::class, CrawlerProperties::class)
class DownloaderConfiguration(
    val downloadProperties: DownloadProperties,
    val crawlerProperties: CrawlerProperties
) {

    companion object {
        private val log = LoggerFactory.getLogger(DownloaderConfiguration::class.java)
    }

    fun httpClientDownloader(): HttpClientDownloader {

    }


    private fun sslConnectionSocketFactory(): SSLConnectionSocketFactory {
        val ignoreSSL = downloadProperties.isIgnoreSsl
        val useSecurity = downloadProperties.isUseSecurity
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