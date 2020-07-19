package com.wxl.dyttcrawler.scheduler.redis;

import com.wxl.dyttcrawler.core.Crawler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.model.HttpRequestBody;

import java.util.Map;

/**
 * Create by wuxingle on 2020/7/19
 */
@ActiveProfiles("redis")
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisPrioritySchedulerTest {

    @Autowired
    private RedisPriorityScheduler redisPriorityScheduler;

    @Autowired
    private Crawler crawler;

    @Test
    public void test1() {
        redisPriorityScheduler.push(newRequest("http://www.baidu.com1", 50, null), crawler);
        redisPriorityScheduler.push(newRequest("http://www.baidu.com2", 20, null), crawler);
        redisPriorityScheduler.push(newRequest("http://www.baidu.com1", 50, null), crawler);
        redisPriorityScheduler.push(newRequest("http://www.baidu.com3", 100, null), crawler);

        redisPriorityScheduler.pushFail(new Request("http://www.baidu.com1"), crawler);
        redisPriorityScheduler.pushFail(new Request("http://www.baidu.com2"), crawler);

        Request r1 = redisPriorityScheduler.poll(crawler);
        Request r2 = redisPriorityScheduler.pollFail(crawler);

        System.out.println(r1);
        System.out.println(r2);
    }


    private Request newRequest(String url, int score, Map<String, Object> extra) {
        Request request = new Request(url);
        request.setPriority(score);
        request.setExtras(extra);
        request.setRequestBody(new HttpRequestBody(new byte[]{1,2,3},"a","utf-8"));
        return request;
    }
}