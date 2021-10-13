package com.wxl.dyttcrawler.downloader

import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.socket.PlainConnectionSocketFactory
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.TrustAllStrategy
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.ssl.SSLContextBuilder
import org.junit.jupiter.api.Test
import us.codecraft.webmagic.Request
import us.codecraft.webmagic.Site
import us.codecraft.webmagic.Task
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocket

/**
 * Create by wuxingle on 2021/10/02
 * -Djavax.net.debug=ssl,handshake
 */
class HttpClientDownloaderTest {

    @Test
    fun test1() {
        val site = Site().apply {
            charset = "gbk"
            domain = "www.dytt8.net"
        }

        val downloader = getDownloader()
        val page = downloader.download(
            Request("http://www.dytt8.net/"),
            object : Task {
                override fun getUUID(): String {
                    return "test"
                }

                override fun getSite(): Site {
                    return site
                }
            })
        val html = page.html
        println(html)
    }

    private fun getDownloader(): HttpClientDownloader {
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

        val httpClientGenerator = HttpClientManager(connectionManager)

        return HttpClientDownloader(httpClientGenerator, "gbk")
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

}