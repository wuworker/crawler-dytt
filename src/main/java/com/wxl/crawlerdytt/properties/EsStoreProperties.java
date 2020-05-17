package com.wxl.crawlerdytt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by wuxingle on 2020/5/17
 * es相关配置
 */
@Data
@ConfigurationProperties(prefix = "crawler.store.es")
public class EsStoreProperties {

    private RequestProperties request = new RequestProperties();

    private Duration retryTimeout = Duration.ofSeconds(5);

    @NestedConfigurationProperty
    private Map<String, IndexProperties> indices = new HashMap<>();

    @Data
    public static class RequestProperties {

        private Duration connectTimeout = Duration.ofSeconds(5);

        private Duration socketTimeout = Duration.ofSeconds(10);

        private Duration connectRequestTimeout = Duration.ofSeconds(5);

        private boolean compressEnabled = true;
    }

    @Data
    public static class IndexProperties {
        private String index;

        private String type = "_doc";
    }
}
