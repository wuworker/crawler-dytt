package com.wxl.dyttcrawler.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wxl.dyttcrawler.scheduler.BatchScheduler;
import com.wxl.dyttcrawler.scheduler.local.BatchHashSetDuplicateRemover;
import com.wxl.dyttcrawler.scheduler.local.BatchPriorityScheduler;
import com.wxl.dyttcrawler.scheduler.redis.OperationRedisScheduler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Create by wuxingle on 2020/5/20
 * 任务队列配置
 */
@Configuration
public class SchedulerConfiguration {

    /**
     * 本地队列
     */
    @Bean
    @ConditionalOnProperty(prefix = "crawler.scheduler", name = "type", havingValue = "local")
    public BatchScheduler localScheduler() {
        BatchPriorityScheduler priorityScheduler = new BatchPriorityScheduler();
        priorityScheduler.setDuplicateRemover(new BatchHashSetDuplicateRemover());

        return priorityScheduler;
    }


    /**
     * redis队列
     */
    @Bean
    @ConditionalOnProperty(prefix = "crawler.scheduler", name = "type", havingValue = "redis")
    public BatchScheduler redisScheduler(RedisConnectionFactory redisConnectionFactory,
                                         ObjectMapper objectMapper) {
        return new OperationRedisScheduler(redisConnectionFactory, objectMapper);
    }

}
