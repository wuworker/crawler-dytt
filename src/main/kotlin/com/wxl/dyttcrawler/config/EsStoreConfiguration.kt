package com.wxl.dyttcrawler.config

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.wxl.dyttcrawler.properties.EsStoreProperties
import org.elasticsearch.client.Node
import org.elasticsearch.client.RestClient
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Create by wuxingle on 2021/10/11
 * es配置
 */
@Configuration
class EsStoreConfiguration(
    private val esStoreProperties: EsStoreProperties
) {

    companion object {
        private val log = LoggerFactory.getLogger(EsStoreConfiguration::class.java)
    }

    @Bean
    fun clientBuilderCustomizer(): RestClientBuilderCustomizer {
        val maxSize = esStoreProperties.pool.maxThreads

        return RestClientBuilderCustomizer {
            it.setFailureListener(object : RestClient.FailureListener() {
                override fun onFailure(node: Node) {
                    log.error("es store fail,node is:{}", node)
                }
            })
                .setHttpClientConfigCallback { builder ->
                    builder.setMaxConnTotal(maxSize)
                        .setThreadFactory(
                            ThreadFactoryBuilder()
                                .setDaemon(true)
                                .setNameFormat("es-pool-%s")
                                .build()
                        )
                        .setMaxConnPerRoute(maxSize)
                }
                .setRequestConfigCallback { builder ->
                    builder.setContentCompressionEnabled(true)
                }
        }
    }

}