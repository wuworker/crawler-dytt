package com.wxl.crawlerdytt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Create by wuxingle on 2020/5/16
 * 下载配置
 */
@Data
@ConfigurationProperties(prefix = "crawler.download")
public class DownloadProperties {

    private boolean ignoreSsl = true;

    /**
     * http连接的keepAlive
     */
    private Duration keepAlive = Duration.ofMinutes(3);

    private Duration validateAfterInactivity = Duration.ofMinutes(1);

}
