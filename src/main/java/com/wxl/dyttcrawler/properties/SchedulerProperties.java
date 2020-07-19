package com.wxl.dyttcrawler.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Create by wuxingle on 2020/5/20
 * 任务队列管理
 */
@Data
@ConfigurationProperties(prefix = "crawler.scheduler")
public class SchedulerProperties {

    private Type type = Type.LOCAL;

    public enum Type {
        LOCAL,
        REDIS
    }

}
