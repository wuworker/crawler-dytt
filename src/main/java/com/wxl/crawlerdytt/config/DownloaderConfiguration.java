package com.wxl.crawlerdytt.config;

import com.wxl.crawlerdytt.core.DyttConstants;
import com.wxl.crawlerdytt.utils.HttpClientDownloader;
import com.wxl.crawlerdytt.utils.HttpClientDownloaderBuilder;
import com.wxl.crawlerdytt.utils.HttpClientGenerator;
import com.wxl.crawlerdytt.utils.SslContextUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.codecraft.webmagic.downloader.HttpUriRequestConverter;

import java.util.concurrent.TimeUnit;

/**
 * Create by wuxingle on 2020/5/2
 * http下载配置
 */
@Configuration
public class DownloaderConfiguration {

    @Bean
    public HttpClientDownloader httpClientDownloader(PoolingHttpClientConnectionManager poolManager) {
        HttpClientGenerator httpClientGenerator = new HttpClientGenerator(poolManager);

        return new HttpClientDownloaderBuilder()
                .httpClientGenerator(httpClientGenerator)
                .requestConverter(new HttpUriRequestConverter())
                .responseHeader(false)
                .defaultCharset(DyttConstants.DEFAULT_CHARSET)
                .build();
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", sslConnectionSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                reg, null, null, null, 5, TimeUnit.MINUTES);
        connectionManager.setDefaultMaxPerRoute(100);
        connectionManager.setValidateAfterInactivity(60000);

        return connectionManager;
    }


    private SSLConnectionSocketFactory sslConnectionSocketFactory() {
        return new SSLConnectionSocketFactory(SslContextUtils.createIgnoreVerifySSL(),
                (s, sslSession) -> true);
    }

}






