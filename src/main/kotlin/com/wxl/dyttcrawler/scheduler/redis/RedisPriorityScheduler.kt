package com.wxl.dyttcrawler.scheduler.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.wxl.dyttcrawler.scheduler.BatchDuplicateRemovedScheduler
import com.wxl.dyttcrawler.scheduler.BatchDuplicateRemover
import com.wxl.dyttcrawler.scheduler.ProcessFailScheduler
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.*
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.data.redis.serializer.RedisSerializer
import us.codecraft.webmagic.Request
import us.codecraft.webmagic.Task
import us.codecraft.webmagic.scheduler.MonitorableScheduler

/**
 * Create by wuxingle on 2021/10/11
 * redis优先队列
 */
open class RedisPriorityScheduler(
    connectionFactory: RedisConnectionFactory,
    private val objectMapper: ObjectMapper = ObjectMapper()
) : BatchDuplicateRemovedScheduler(),
    MonitorableScheduler, BatchDuplicateRemover, ProcessFailScheduler {

    companion object {
        /**
         * 访问过的 set key
         */
        private const val VISITED_QUEUE_KEY = "dytt:visitedQueue:"

        /**
         * 待处理 zset key
         */
        private const val TODO_QUEUE_KEY = "dytt:todoQueue:"

        /**
         * url详情 hash key
         */
        private const val URL_DETAIL_KEY = "dytt:detailUrl:"

        /**
         * 处理失败 list key
         */
        private const val FAIL_QUEUE_KEY = "dytt:failQueue:"
    }

    protected val template: RedisTemplate<String, String>

    /**
     * zset类型保存todo队列
     */
    protected val zSetOps: ZSetOperations<String, String>

    /**
     * set类型保存visited队列
     */
    protected val setOps: SetOperations<String, String>

    /**
     * hash类型保存url对应的request
     */
    protected val hashOps: HashOperations<String, String, String>

    /**
     * list类型保存失败队列
     */
    protected val listOps: ListOperations<String, String>

    private val pollScript: RedisScript<String> = PollScript()

    private val redisSerializer: RedisSerializer<String> = RedisSerializer.string()

    init {

        template = RedisTemplate<String, String>().apply {
            setConnectionFactory(connectionFactory)

            keySerializer = redisSerializer
            valueSerializer = redisSerializer
            hashKeySerializer = redisSerializer
            hashValueSerializer = redisSerializer
            setDefaultSerializer(redisSerializer)

            isEnableDefaultSerializer = false
            afterPropertiesSet()
        }

        zSetOps = template.opsForZSet()
        setOps = template.opsForSet()
        hashOps = template.opsForHash()
        listOps = template.opsForList()

        setDuplicateRemover(this)
    }

    override fun pushWhenNoDuplicate(request: Request, task: Task) {
        hashOps.put(detailKey(task), request.url, serializerRequest(request))
        zSetOps.add(todoKey(task), request.url, request.priority.toDouble())
    }

    override fun pushWhenNoDuplicate(requests: Collection<Request>, task: Task) {
        val reqMap = requests.associate { it.url to serializerRequest(it) }
        hashOps.putAll(detailKey(task), reqMap)

        val typedTupleSet = requests
            .map { DefaultTypedTuple(it.url, it.priority.toDouble()) }
            .toSet()
        zSetOps.add(todoKey(task), typedTupleSet)
    }

    override fun poll(task: Task): Request? {
        val request =
            (template as RedisOperations<String, String>).execute(pollScript, listOf(todoKey(task), detailKey(task)))
                ?: return null
        return deserializerRequest(request)
    }

    override fun isDuplicate(request: Request, task: Task): Boolean {
        val add = setOps.add(visitedKey(task), request.url)
        return (add ?: 0L) == 0L
    }

    override fun filterDuplicate(requests: List<Request>, task: Task): List<Request> {
        val result = template.executePipelined {
            for (request in requests) {
                it.sAdd(
                    redisSerializer.serialize(visitedKey(task))!!,
                    redisSerializer.serialize(request.url)
                )
            }
            return@executePipelined null
        }

        val filterRequests = mutableListOf<Request>()
        for (i in requests.indices) {
            if (result[i] == 1L) {
                filterRequests.add(requests[i])
            }
        }

        return filterRequests
    }

    override fun resetDuplicateCheck(task: Task) {
        template.delete(visitedKey(task))
    }

    override fun getLeftRequestsCount(task: Task): Int {
        val count = zSetOps.zCard(todoKey(task))
        return (count ?: 0).toInt()
    }

    override fun getTotalRequestsCount(task: Task): Int {
        val count = setOps.size(visitedKey(task))
        return (count ?: 0).toInt()
    }

    override fun getFailCount(task: Task): Int {
        val count = listOps.size(failKey(task))
        return (count ?: 0).toInt()
    }

    override fun pushFail(request: Request, task: Task) {
        listOps.leftPush(failKey(task), serializerRequest(request))
    }

    override fun pollFail(task: Task): Request? {
        val request = listOps.rightPop(failKey(task)) ?: return null
        return deserializerRequest(request)
    }

    protected open fun serializerRequest(request: Request): String = objectMapper.writeValueAsString(request)

    protected open fun deserializerRequest(request: String): Request =
        objectMapper.readValue(request, Request::class.java)

    protected open fun visitedKey(task: Task) = VISITED_QUEUE_KEY + task.uuid

    protected open fun todoKey(task: Task) = TODO_QUEUE_KEY + task.uuid

    protected open fun detailKey(task: Task) = URL_DETAIL_KEY + task.uuid

    protected open fun failKey(task: Task) = FAIL_QUEUE_KEY + task.uuid
}


private class PollScript : RedisScript<String> {

    override fun getSha1() = "0869a5d8faa52f67588bf37ff94d84f52ebd87da"

    override fun getResultType() = String::class.java

    override fun getScriptAsString() = """
        local top=redis.call('zrevrange',KEYS[1],0,0)
        if top[1]~=nil then
            redis.call('zrem',KEYS[1],top[1])
            local detail = redis.call('hget',KEYS[2],top[1])
            redis.call('hdel',KEYS[2],top[1])
            return detail
        end
        return nil
    """.trimIndent()
}

