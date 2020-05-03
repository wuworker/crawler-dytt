package com.wxl.crawlerdytt.config;

import com.wxl.crawlerdytt.core.HtmlDownLoader;
import com.wxl.crawlerdytt.core.impl.HttpClientDownLoader;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Create by wuxingle on 2020/5/2
 * http配置
 */
@Configuration
public class HtmlDownerConfiguration {


    @Bean
    public CloseableHttpClient httpClient() {
        PoolingHttpClientConnectionManager poolingManager =
                new PoolingHttpClientConnectionManager(5, TimeUnit.MINUTES);
        poolingManager.setMaxTotal(10);
        poolingManager.setDefaultMaxPerRoute(10);
        // 超过这个时间检查连接可用性
        poolingManager.setValidateAfterInactivity(60000);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(5000)
                .setRedirectsEnabled(true)
                .setConnectionRequestTimeout(5000)
                .build();

        return HttpClients.custom()
                // 禁止认证相关
                .disableConnectionState()
                .disableAuthCaching()
                // 禁止cookie管理
                .disableCookieManagement()
                .setConnectionManager(poolingManager)
                .setDefaultRequestConfig(requestConfig)
                // 连接驱逐策略
                .evictExpiredConnections()
                .evictIdleConnections(2, TimeUnit.MINUTES)
                // 重试
                .setRetryHandler((exception, executionCount, context) -> {
                    if (executionCount > 3) {
                        return false;
                    }
                    if (exception.getClass() == NoHttpResponseException.class) {
                        return true;
                    }
                    return false;
                })
                .build();
    }


    @Bean
    public HtmlDownLoader httpClientDownloader(CloseableHttpClient httpClient) {
        return new HttpClientDownLoader(httpClient);
    }
}
