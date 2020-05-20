package com.wxl.crawlerdytt.config;

import com.wxl.crawlerdytt.scheduler.redis.RedisPriorityScheduler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import us.codecraft.webmagic.scheduler.PriorityScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;

/**
 * Create by wuxingle on 2020/5/20
 * 任务队列配置
 */
@Configuration
public class SchedulerConfiguration {


    @Bean
    @ConditionalOnProperty(prefix = "crawler.scheduler", name = "type", havingValue = "local")
    public Scheduler localScheduler() {
        PriorityScheduler priorityScheduler = new PriorityScheduler();
        priorityScheduler.setDuplicateRemover(new HashSetDuplicateRemover());

        return priorityScheduler;
    }


    @Bean
    @ConditionalOnProperty(prefix = "crawler.scheduler", name = "type", havingValue = "redis")
    public Scheduler redisScheduler(RedisConnectionFactory redisConnectionFactory) {
        return new RedisPriorityScheduler(redisConnectionFactory);
    }

}
