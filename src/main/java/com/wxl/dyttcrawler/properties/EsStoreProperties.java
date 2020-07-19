package com.wxl.dyttcrawler.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Create by wuxingle on 2020/5/17
 * es相关配置
 */
@Data
@ConfigurationProperties(prefix = "crawler.store.es")
public class EsStoreProperties {

    private PoolProperties pool = new PoolProperties();

    private RequestProperties request = new RequestProperties();

    private Duration retryTimeout = Duration.ofSeconds(5);

    @Data
    public static class PoolProperties {

        private int maxThreads = 10;
    }

    @Data
    public static class RequestProperties {

        private Duration connectTimeout = Duration.ofSeconds(5);

        private Duration socketTimeout = Duration.ofSeconds(10);

        private Duration connectRequestTimeout = Duration.ofSeconds(5);

        private boolean compressEnabled = true;
    }
}
