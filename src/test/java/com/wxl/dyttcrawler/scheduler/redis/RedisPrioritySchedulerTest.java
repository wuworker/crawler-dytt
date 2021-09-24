package com.wxl.dyttcrawler.scheduler.redis;

import com.wxl.dyttcrawler.core.Crawler;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.model.HttpRequestBody;

import java.util.List;
import java.util.Map;

/**
 * Create by wuxingle on 2020/7/19
 */
@ActiveProfiles("redis")
@ExtendWith(SpringExtension.class)
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

    @Test
    public void test2() {
        List<Request> requests = Lists.newArrayList(
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
        );


        redisPriorityScheduler.push(requests, crawler);

        Request r1 = redisPriorityScheduler.poll(crawler);

        System.out.println(r1);
    }


    private Request newRequest(String url, int score, Map<String, Object> extra) {
        Request request = new Request(url);
        request.setPriority(score);
        request.setExtras(extra);
        request.setRequestBody(new HttpRequestBody(new byte[]{1, 2, 3}, "a", "utf-8"));
        return request;
    }
}