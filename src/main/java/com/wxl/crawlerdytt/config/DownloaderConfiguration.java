package com.wxl.crawlerdytt.config;

import com.wxl.crawlerdytt.downloader.HttpClientDownloader;
import com.wxl.crawlerdytt.downloader.HttpClientDownloaderBuilder;
import com.wxl.crawlerdytt.downloader.HttpClientGenerator;
import com.wxl.crawlerdytt.properties.CrawlerProperties;
import com.wxl.crawlerdytt.properties.DownloadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.codecraft.webmagic.downloader.HttpUriRequestConverter;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Create by wuxingle on 2020/5/2
 * http下载配置
 */
@Slf4j
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

    /**
     * http下载器
     */
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

    /**
     * http连接池
     */
    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {

        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", sslConnectionSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                reg, null, null, null,
                downloadProperties.getKeepAlive().getSeconds(), TimeUnit.SECONDS);
        connectionManager.setMaxTotal(crawlerProperties.getMaxThread());
        connectionManager.setDefaultMaxPerRoute(crawlerProperties.getMaxThread());
        connectionManager.setValidateAfterInactivity((int) downloadProperties.getValidateAfterInactivity().toMillis());

        return connectionManager;
    }

    /**
     * 获取SSLSocketFactory
     */
    private SSLConnectionSocketFactory sslConnectionSocketFactory() {
        boolean ignoreSSL = downloadProperties.isIgnoreSsl();
        boolean useSecurity = downloadProperties.isUseSecurity();
        if (ignoreSSL) {
            try {
                SSLContextBuilder builder = new SSLContextBuilder();
                builder.loadTrustMaterial(TrustAllStrategy.INSTANCE);
                SSLContext sslContext = builder.build();

                String[] protocols = null;
                String[] cipherSuites = null;

                // 允许使用不安全的协议和加密套件
                if (!useSecurity) {
                    protocols = getEnabledProtocols(sslContext);
                    cipherSuites = getEnabledCipherSuites(sslContext);

                    if (log.isDebugEnabled()) {
                        log.debug("https use protocols:{}", Arrays.toString(protocols));
                        log.debug("https use cipher suites:{}", Arrays.toString(cipherSuites));
                    }
                }

                return new SSLConnectionSocketFactory(
                        sslContext, protocols, cipherSuites, NoopHostnameVerifier.INSTANCE);

            } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
                throw new IllegalStateException(e);
            }
        } else {
            return SSLConnectionSocketFactory.getSocketFactory();
        }
    }

    /**
     * 获取https协议TLSv1,TLSv1.1,TLSv1.2
     * 可能包含不安全的协议
     */
    private String[] getEnabledProtocols(SSLContext sslContext) {
        try (SSLSocket socket = (SSLSocket) sslContext.getSocketFactory().createSocket()) {
            return socket.getEnabledProtocols();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 获取https加密套件，包含不安全的加密算法
     */
    private String[] getEnabledCipherSuites(SSLContext sslContext) {
        try (SSLSocket socket = (SSLSocket) sslContext.getSocketFactory().createSocket()) {
            return socket.getEnabledCipherSuites();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}




