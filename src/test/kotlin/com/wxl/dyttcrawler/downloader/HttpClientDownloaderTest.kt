package com.wxl.dyttcrawler.downloader

import com.wxl.dyttcrawler.downloadPage
import com.wxl.dyttcrawler.getDownloader
import org.junit.jupiter.api.Test
import us.codecraft.webmagic.Request
import us.codecraft.webmagic.Site
import us.codecraft.webmagic.Task

/**
 * Create by wuxingle on 2021/10/02
 * -Djavax.net.debug=ssl,handshake
 */
class HttpClientDownloaderTest {

    @Test
    fun test1() {
        val html = downloadPage("http://www.dytt8.net").html
        println(html)
    }

}