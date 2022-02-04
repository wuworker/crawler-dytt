package com.wxl.dyttcrawler.core

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Create by wuxingle on 2021/10/12
 *
 */
@ActiveProfiles("redis")
@ExtendWith(SpringExtension::class)
@SpringBootTest
class CrawlerTest {

    @Autowired
    lateinit var crawler: Crawler

    @Test
    fun test() {
        crawler.start()

        runBlocking {
            delay(60 * 1000L)
            crawler.stop()
            crawler.awaitStopped()
        }
    }
}