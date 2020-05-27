package com.wxl.crawlerdytt.core;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Create by wuxingle on 2020/5/27
 * redis爬虫管理
 */
public class RedisCrawlerManager implements CrawlerManager {


    private RedisTemplate<String, String> redisTemplate;


    public RedisCrawlerManager(RedisConnectionFactory connectionFactory) {
        this.redisTemplate = new StringRedisTemplate(connectionFactory);


    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public boolean shutdown() {
        return false;
    }

    @Override
    public Status getStatus() {
        return null;
    }
}
