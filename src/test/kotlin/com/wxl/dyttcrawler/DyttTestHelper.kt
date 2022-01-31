package com.wxl.dyttcrawler

import com.wxl.dyttcrawler.downloader.DyttRedirectStrategy
import com.wxl.dyttcrawler.downloader.HttpClientDownloader
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.socket.PlainConnectionSocketFactory
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.TrustAllStrategy
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.ssl.SSLContextBuilder
import us.codecraft.webmagic.Page
import us.codecraft.webmagic.Request
import us.codecraft.webmagic.Site
import us.codecraft.webmagic.Task
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocket

/**
 * Create by wuxingle on 2022/1/31
 * 测试帮助类
 */

fun downloadPage(url: String): Page {
    val site = Site().apply {
        charset = "gbk"
        domain = "www.dytt8.net"
    }
    val req = Request().apply {
        this.url = url
        charset = "gbk"
        method = "get"
    }
    val downloader = getDownloader()
    return downloader.download(
        req,
        object : Task {
            override fun getUUID(): String {
                return "test"
            }

            override fun getSite(): Site {
                return site
            }
        })
}

fun getDownloader(): HttpClientDownloader {
    val reg = RegistryBuilder.create<ConnectionSocketFactory>().run {
        register("http", PlainConnectionSocketFactory.INSTANCE)
        register("https", sslConnectionSocketFactory())
        build()
    }

    val connectionManager = PoolingHttpClientConnectionManager(
        reg, null, null, null,
        60, TimeUnit.SECONDS
    ).apply {
        maxTotal = 10
        defaultMaxPerRoute = 10
    }

    val httpClient = HttpClients.custom().apply {
        setConnectionManager(connectionManager)
        setRedirectStrategy(DyttRedirectStrategy())
    }.build()

    return HttpClientDownloader(httpClient, "gbk")
}

private fun sslConnectionSocketFactory(): SSLConnectionSocketFactory {
    val builder = SSLContextBuilder()
    builder.loadTrustMaterial(TrustAllStrategy.INSTANCE)
    val sslContext = builder.build()

    val socket = sslContext.socketFactory.createSocket() as SSLSocket
    val enabledProtocols = socket.enabledProtocols
    val enabledCipherSuites = socket.enabledCipherSuites
    println("enable len:" + enabledCipherSuites.size)
    println("enable :" + Arrays.toString(enabledCipherSuites))
    return SSLConnectionSocketFactory(
        sslContext, enabledProtocols, enabledCipherSuites,
        NoopHostnameVerifier.INSTANCE
    )
}



