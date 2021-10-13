package com.wxl.dyttcrawler.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wxl.dyttcrawler.scheduler.BatchScheduler
import com.wxl.dyttcrawler.scheduler.local.BatchHashSetDuplicateRemover
import com.wxl.dyttcrawler.scheduler.local.BatchPriorityScheduler
import com.wxl.dyttcrawler.scheduler.redis.OperationRedisScheduler
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory

/**
 * Create by wuxingle on 2021/10/11
 * 任务队列配置
 */
@Configuration
class SchedulerConfiguration {

    /**
     * 本地队列
     */
    @Bean
    @ConditionalOnProperty(prefix = "crawler.scheduler", name = ["type"], havingValue = "local")
    fun localScheduler(): BatchScheduler {
        val priorityScheduler = BatchPriorityScheduler()
        priorityScheduler.duplicateRemover = BatchHashSetDuplicateRemover()
        return priorityScheduler
    }

    /**
     * redis队列
     */
    @Bean
    @ConditionalOnProperty(prefix = "crawler.scheduler", name = ["type"], havingValue = "redis")
    fun redisScheduler(
        connectionFactory: RedisConnectionFactory, objectMapper: ObjectMapper
    ): BatchScheduler {
        return OperationRedisScheduler(connectionFactory, objectMapper)
    }


}