package com.wxl.dyttcrawler.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Create by wuxingle on 2021/10/12
 *
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest
class CrawlerTest {

    @Autowired
    lateinit var crawler: Crawler

    @Test
    fun test() {
        crawler.start(5)
        crawler.close()
        crawler.awaitStopped()
    }
}