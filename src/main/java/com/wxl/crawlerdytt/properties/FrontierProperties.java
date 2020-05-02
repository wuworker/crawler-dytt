package com.wxl.crawlerdytt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Create by wuxingle on 2020/5/2
 * front配置
 */
@Data
@ConfigurationProperties(prefix = "crawler.front")
public class FrontierProperties {

    private String removeTopScript;

    private String scriptSha1;
}
