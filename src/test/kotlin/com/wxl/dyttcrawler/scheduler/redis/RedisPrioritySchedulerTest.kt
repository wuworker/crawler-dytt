package com.wxl.dyttcrawler.scheduler.redis

import com.wxl.dyttcrawler.core.Crawler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import us.codecraft.webmagic.Request
import us.codecraft.webmagic.model.HttpRequestBody

/**
 * Create by wuxingle on 2021/10/12
 *
 */
@ActiveProfiles("redis")
@ExtendWith(SpringExtension::class)
@SpringBootTest
class RedisPrioritySchedulerTest {

    @Autowired
    lateinit var redisPriorityScheduler: RedisPriorityScheduler

    @Autowired
    lateinit var crawler: Crawler

    @Test
    fun test1() {
        redisPriorityScheduler.push(newRequest("http://www.baidu.com1", 50, null), crawler)
        redisPriorityScheduler.push(newRequest("http://www.baidu.com2", 20, null), crawler)
        redisPriorityScheduler.push(newRequest("http://www.baidu.com1", 50, null), crawler)
        redisPriorityScheduler.push(newRequest("http://www.baidu.com3", 100, null), crawler)

        redisPriorityScheduler.pushFail(Request("http://www.baidu.com1"), crawler)
        redisPriorityScheduler.pushFail(Request("http://www.baidu.com2"), crawler)

        val r1 = redisPriorityScheduler.poll(crawler)
        val r2 = redisPriorityScheduler.pollFail(crawler)

        println(r1)
        println(r2)
    }

    @Test
    fun test2() {
        val requests = listOf(
            newRequest("http://www.baidu.com1", 80, null),
            newRequest("http://www.baidu.com2", 50, null),
            newRequest("http://www.baidu.com3", 10, null),
            newRequest("http://www.baidu.com4", 40, null),
            newRequest("http://www.baidu.com5", 60, null),
            newRequest("http://www.baidu.com1", 30, null),
            newRequest("http://www.baidu.com6", 20, null),
            newRequest("http://www.baidu.com2", 90, null),
            newRequest("http://www.baidu.com7", 70, null),
            newRequest("http://www.baidu.com3", 50, null)
        )

        redisPriorityScheduler.push(requests, crawler)

        val r1 = redisPriorityScheduler.poll(crawler)

        println(r1)
    }

    private fun newRequest(url: String, score: Int, extra: Map<String, Any>?): Request {
        return Request(url).apply {
            priority = score.toLong()
            extras = extra
            requestBody = HttpRequestBody(byteArrayOf(1, 2, 3), "a", "utf-8")
        }
    }


}