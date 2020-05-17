package com.wxl.crawlerdytt.config;

import com.wxl.crawlerdytt.downloader.HttpClientDownloader;
import com.wxl.crawlerdytt.downloader.HttpClientDownloaderBuilder;
import com.wxl.crawlerdytt.downloader.HttpClientGenerator;
import com.wxl.crawlerdytt.properties.CrawlerProperties;
import com.wxl.crawlerdytt.properties.DownloadProperties;
import com.wxl.crawlerdytt.utils.SslContextUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.codecraft.webmagic.downloader.HttpUriRequestConverter;

import java.util.concurrent.TimeUnit;

/**
 * Create by wuxingle on 2020/5/2
 * http下载配置
 */
@Configuration
@EnableConfigurationProperties({DownloadProperties.class, CrawlerProperties.class})
public class DownloaderConfiguration {

    private DownloadProperties downloadProperties;

    private CrawlerProperties crawlerProperties;

    @Autowired
    public DownloaderConfiguration(DownloadProperties downloadProperties,
                                   CrawlerProperties crawlerProperties) {
        this.downloadProperties = downloadProperties;
        this.crawlerProperties = crawlerProperties;
    }

    @Bean
    public HttpClientDownloader httpClientDownloader(PoolingHttpClientConnectionManager poolManager) {
        HttpClientGenerator httpClientGenerator = new HttpClientGenerator(poolManager);

        return new HttpClientDownloaderBuilder()
                .httpClientGenerator(httpClientGenerator)
                .requestConverter(new HttpUriRequestConverter())
                .responseHeader(false)
                .defaultCharset(crawlerProperties.getCharset())
                .build();
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {

        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", sslConnectionSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                reg, null, null, null,
                downloadProperties.getKeepAlive().getSeconds(), TimeUnit.SECONDS);
        connectionManager.setDefaultMaxPerRoute(crawlerProperties.getPool().getMaxSize());
        connectionManager.setValidateAfterInactivity((int) downloadProperties.getValidateAfterInactivity().toMillis());

        return connectionManager;
    }


    private SSLConnectionSocketFactory sslConnectionSocketFactory() {
        boolean ignoreSSL = downloadProperties.isIgnoreSsl();
        if (ignoreSSL) {
            return new SSLConnectionSocketFactory(SslContextUtils.createIgnoreVerifySSL(),
                    (s, sslSession) -> true);
        } else {
            return SSLConnectionSocketFactory.getSocketFactory();
        }
    }

}






