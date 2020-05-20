package com.wxl.crawlerdytt;

import com.wxl.crawlerdytt.utils.DyttUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Create by wuxingle on 2020/5/10
 * main
 */
@SpringBootApplication
public class CrawlerDyttApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(CrawlerDyttApplication.class);
        application.addInitializers(DyttUtils::setApplicationContext);
        application.run(args);

        DyttUtils.startCrawler();
    }

}
