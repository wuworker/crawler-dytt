package com.wxl.dyttcrawler.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Create by wuxingle on 2020/5/16
 * 爬虫配置
 */
@Data
@ConfigurationProperties(prefix = "crawler")
public class CrawlerProperties {

    private String taskId;

    private String firstUrl = "https://www.dytt8.net";

    private String charset = "gbk";

    private int maxThreads = 5;

    private SiteProperties site = new SiteProperties();

    @Data
    public static class SiteProperties {

        private String userAgent;

        private Duration sleepTime = Duration.ofSeconds(1);

        private Integer retryTimes = 1;

        private Duration retrySleepTime = Duration.ofSeconds(1);

        private Duration timeout = Duration.ofSeconds(10);

        private List<Integer> acceptStatusCode = Collections.singletonList(200);

        private Map<String, String> headers = Collections.emptyMap();

        private boolean disableCookie = true;
    }
}
