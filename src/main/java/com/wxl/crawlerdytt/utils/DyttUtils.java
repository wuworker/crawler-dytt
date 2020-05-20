package com.wxl.crawlerdytt.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import us.codecraft.webmagic.Spider;

/**
 * Create by wuxingle on 2020/5/18
 * 工具类
 */
public class DyttUtils {

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        DyttUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        Assert.notNull(applicationContext, "application context can not null");
        return applicationContext;
    }

    public static void startCrawler() {
        Assert.notNull(applicationContext, "application context can not null");
        applicationContext.getBean(Spider.class).run();
    }


    public static void stopCrawler() {
        Assert.notNull(applicationContext, "application context can not null");
        applicationContext.getBean(Spider.class).stop();
    }

}
