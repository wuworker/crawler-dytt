package com.wxl.dyttcrawler.scheduler.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.connection.RedisConnectionFactory
import us.codecraft.webmagic.Task

/**
 * Create by wuxingle on 2021/10/11
 * 可操作队列的scheduler
 */
class OperationRedisScheduler(
    connectionFactory: RedisConnectionFactory,
    objectMapper: ObjectMapper = ObjectMapper()
) : RedisPriorityScheduler(connectionFactory, objectMapper) {

    /**
     * 获取待处理队列
     */
    fun rangeTodoQueue(task: Task, start: Long, count: Long): Collection<String> {
        return zSetOps.range(todoKey(task), start, start + count - 1)?.filterNotNull() ?: emptyList()
    }

    /**
     * 获取处理失败的队列
     */
    fun rangeFailQueue(task: Task, start: Long, count: Long): Collection<String> {
        return listOps.range(failKey(task), start, start + count - 1)?.filterNotNull() ?: emptyList()
    }

    /**
     * 移除待处理url
     */
    fun removeTodoUrl(task: Task, url: String): Boolean {
        val len = zSetOps.remove(todoKey(task), url)
        return len == 1L
    }

    /**
     * 移除处理失败url
     */
    fun removeFailQueue(task: Task, url: String): Boolean {
        val len = listOps.remove(todoKey(task), 0, url)
        return len != null && len > 0
    }

}


