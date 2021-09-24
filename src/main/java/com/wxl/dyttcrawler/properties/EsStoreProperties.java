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

    @Data
    public static class PoolProperties {

        private int maxThreads = 10;
    }
}
