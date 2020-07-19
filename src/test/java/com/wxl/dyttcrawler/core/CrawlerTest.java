package com.wxl.dyttcrawler.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Create by wuxingle on 2020/7/18
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlerTest {

    @Autowired
    private Crawler crawler;

    @Test
    public void test() throws Exception {
        boolean start = crawler.start(5);
        crawler.awaitStopped();
        crawler.close();
    }
}
