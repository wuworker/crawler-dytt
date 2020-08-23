package com.wxl.dyttcrawler.scheduler.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import us.codecraft.webmagic.Task;

import java.util.Collection;

/**
 * Create by wuxingle on 2020/8/16
 * 可操作队列的scheduler
 */
public class OperationRedisScheduler extends RedisPriorityScheduler {

    public OperationRedisScheduler(RedisConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    public OperationRedisScheduler(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        super(connectionFactory, objectMapper);
    }


    /**
     * 获取待处理队列
     */
    public Collection<String> rangeTodoQueue(Task task, int start, int count) {
        return zSetOps.range(todoKey(task), start, start + count - 1);
    }

    /**
     * 获取处理失败的队列
     */
    public Collection<String> rangeFailQueue(Task task, int start, int count) {
        return listOps.range(failKey(task), start, start + count - 1);
    }

    /**
     * 移除待处理url
     */
    public boolean removeTodoUrl(Task task, String url) {
        Long len = zSetOps.remove(todoKey(task), url);
        return len != null && len == 1;
    }

    /**
     * 移除待处理失败url
     */
    public boolean removeFailQueue(Task task, String url) {
        Long len = listOps.remove(todoKey(task), 0, url);
        return len != null && len > 0;

    }
}
