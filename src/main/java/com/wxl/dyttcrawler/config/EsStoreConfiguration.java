package com.wxl.dyttcrawler.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wxl.dyttcrawler.properties.EsStoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Create by wuxingle on 2020/5/10
 * es配置
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(EsStoreProperties.class)
public class EsStoreConfiguration {

    private EsStoreProperties esStoreProperties;

    public EsStoreConfiguration(EsStoreProperties esStoreProperties) {
        this.esStoreProperties = esStoreProperties;
    }

    @Bean
    public RestClientBuilderCustomizer clientBuilderCustomizer() {
        int maxSize = esStoreProperties.getPool().getMaxThreads();

        EsStoreProperties.RequestProperties request = esStoreProperties.getRequest();
        int connectTimeout = (int) request.getConnectTimeout().toMillis();
        int socketTimeout = (int) request.getSocketTimeout().toMillis();
        int connectRequestTimeout = (int) request.getConnectRequestTimeout().toMillis();
        boolean compressEnabled = request.isCompressEnabled();

        int retryTimeout = (int) esStoreProperties.getRetryTimeout().toMillis();

        return builder -> builder.setFailureListener(
                new RestClient.FailureListener() {
                    @Override
                    public void onFailure(Node node) {
                        log.error("es store fail,node is:", node);
                    }
                })
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    httpClientBuilder.setMaxConnTotal(maxSize)
                            .setThreadFactory(new ThreadFactoryBuilder()
                                    .setDaemon(true)
                                    .setNameFormat("es-pool-%s")
                                    .build()
                            )
                            .setMaxConnPerRoute(maxSize);
                    return httpClientBuilder;
                })
                .setRequestConfigCallback(requestConfigBuilder -> {
                    requestConfigBuilder.setConnectTimeout(connectTimeout)
                            .setSocketTimeout(socketTimeout)
                            .setConnectionRequestTimeout(connectRequestTimeout)
                            .setContentCompressionEnabled(compressEnabled);
                    return requestConfigBuilder;
                })
                .setMaxRetryTimeoutMillis(retryTimeout);

    }

}
