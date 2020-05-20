package com.wxl.crawlerdytt.scheduler.redis;

import com.google.common.collect.Lists;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

/**
 * Created by w1451 on 2020/05/20
 * redis优先队列
 */
public class RedisPriorityScheduler extends DuplicateRemovedScheduler
        implements MonitorableScheduler, DuplicateRemover {

    private static final String VISITED_QUEUE_KEY = "dytt:visitedQueue:";

    private static final String TODO_QUEUE_KEY = "dytt:todoQueue:";

    private static final String URL_DETAIL_KEY = "dytt:detailUrl:";

    private RedisTemplate<String, String> template;

    /**
     * zset类型保存todo队列
     */
    private ZSetOperations<String, String> zSetOps;

    /**
     * set类型保存visited队列
     */
    private SetOperations<String, String> setOps;

    /**
     * hash类型保存url对应的request
     */
    private HashOperations<String, String, Request> hashOps;

    private RedisScript<Request> pollScript;

    private RedisSerializer<Request> requestSerializer;

    public RedisPriorityScheduler(RedisConnectionFactory connectionFactory) {
        this(connectionFactory, new RequestSerializer());
    }

    public RedisPriorityScheduler(RedisConnectionFactory connectionFactory,
                                  RedisSerializer<Request> redisSerializer) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(redisSerializer);

        template.setEnableDefaultSerializer(false);
        template.afterPropertiesSet();

        this.template = template;
        this.requestSerializer = redisSerializer;
        this.zSetOps = this.template.opsForZSet();
        this.setOps = this.template.opsForSet();
        this.hashOps = this.template.opsForHash();
        this.pollScript = new PollScript();
        setDuplicateRemover(this);
    }

    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        hashOps.put(detailKey(task), request.getUrl(), request);
        zSetOps.add(todoKey(task), request.getUrl(), request.getPriority());
    }

    @Override
    public Request poll(Task task) {
        return template.execute(pollScript, RedisSerializer.java(), requestSerializer,
                Lists.newArrayList(todoKey(task), detailKey(task)));
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        Long add = setOps.add(visitedKey(task), request.getUrl());
        return add == null || add == 0;
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        template.delete(visitedKey(task));
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        Long count = zSetOps.zCard(todoKey(task));
        return count == null ? 0 : count.intValue();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        Long count = setOps.size(visitedKey(task));
        return count == null ? 0 : count.intValue();
    }

    private static String visitedKey(Task task) {
        return VISITED_QUEUE_KEY + task.getUUID();
    }

    private static String todoKey(Task task) {
        return TODO_QUEUE_KEY + task.getUUID();
    }

    private static String detailKey(Task task) {
        return URL_DETAIL_KEY + task.getUUID();
    }

    private static class PollScript implements RedisScript<Request> {

        @Override
        public String getSha1() {
            return "0869a5d8faa52f67588bf37ff94d84f52ebd87da";
        }

        @Override
        public Class<Request> getResultType() {
            return Request.class;
        }

        @Override
        public String getScriptAsString() {
            return "local top=redis.call('zrevrange',KEYS[1],0,0)\n" +
                    "if top[1]~=nil then\n" +
                    "redis.call('zrem',KEYS[1],top[1])\n" +
                    "local detail = redis.call('hget',KEYS[2],top[1])\n" +
                    "redis.call('hdel',KEYS[2],top[1])\n" +
                    "return detail\n" +
                    "end\n" +
                    "return nil";
        }
    }
}
