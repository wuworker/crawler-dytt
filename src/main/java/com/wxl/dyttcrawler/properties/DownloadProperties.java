package com.wxl.dyttcrawler.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import us.codecraft.webmagic.proxy.Proxy;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by wuxingle on 2020/5/16
 * 下载配置
 */
@Data
@ConfigurationProperties(prefix = "crawler.download")
public class DownloadProperties {

    /**
     * 忽略https证书校验
     */
    private boolean ignoreSsl = true;

    /**
     * 使用安全的https协议和加密套件
     */
    private boolean useSecurity = false;

    /**
     * 连接池
     */
    private PoolProperties pool = new PoolProperties();

    /**
     * 代理配置
     */
    private List<Proxy> proxies = new ArrayList<>();

    @Data
    public static class PoolProperties {

        /**
         * 最大连接数
         */
        private int maxThreads = 10;

        /**
         * http连接的keepAlive
         */
        private Duration keepAlive = Duration.ofMinutes(3);

        /**
         * 空闲n时间后需要校验
         */
        private Duration validateAfterInactivity = Duration.ofMinutes(1);
    }

}
