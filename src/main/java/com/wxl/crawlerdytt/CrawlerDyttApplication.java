package com.wxl.crawlerdytt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import us.codecraft.webmagic.Spider;

/**
 * Create by wuxingle on 2020/5/10
 * main
 */
@SpringBootApplication
public class CrawlerDyttApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CrawlerDyttApplication.class, args);

        Spider spider = context.getBean(Spider.class);
        spider.run();
    }

}
