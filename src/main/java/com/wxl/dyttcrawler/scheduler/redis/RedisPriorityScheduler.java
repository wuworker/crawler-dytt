package com.wxl.dyttcrawler.scheduler.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.wxl.dyttcrawler.scheduler.BatchDuplicateRemovedScheduler;
import com.wxl.dyttcrawler.scheduler.BatchDuplicateRemover;
import com.wxl.dyttcrawler.scheduler.ProcessFailScheduler;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by w1451 on 2020/05/20
 * redis优先队列
 */
public class RedisPriorityScheduler extends BatchDuplicateRemovedScheduler
        implements MonitorableScheduler, BatchDuplicateRemover, ProcessFailScheduler {

    /**
     * 访问过的 set key
     */
    private static final String VISITED_QUEUE_KEY = "dytt:visitedQueue:";
    /**
     * 待处理 zset key
     */
    private static final String TODO_QUEUE_KEY = "dytt:todoQueue:";
    /**
     * url详情 hash key
     */
    private static final String URL_DETAIL_KEY = "dytt:detailUrl:";
    /**
     * 处理失败 list key
     */
    private static final String FAIL_QUEUE_KEY = "dytt:failQueue:";

    protected RedisTemplate<String, String> template;

    /**
     * zset类型保存todo队列
     */
    protected ZSetOperations<String, String> zSetOps;

    /**
     * set类型保存visited队列
     */
    protected SetOperations<String, String> setOps;

    /**
     * hash类型保存url对应的request
     */
    protected HashOperations<String, String, String> hashOps;

    /**
     * list类型保存失败队列
     */
    protected ListOperations<String, String> listOps;

    private RedisScript<String> pollScript;

    private ObjectMapper objectMapper;

    private RedisSerializer<String> redisSerializer;

    public RedisPriorityScheduler(RedisConnectionFactory connectionFactory) {
        this(connectionFactory, new ObjectMapper());
    }

    public RedisPriorityScheduler(RedisConnectionFactory connectionFactory,
                                  ObjectMapper objectMapper) {
        this.redisSerializer = RedisSerializer.string();

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(redisSerializer);
        template.setValueSerializer(redisSerializer);
        template.setHashKeySerializer(redisSerializer);
        template.setHashValueSerializer(redisSerializer);
        template.setDefaultSerializer(redisSerializer);

        template.setEnableDefaultSerializer(false);
        template.afterPropertiesSet();

        this.template = template;
        this.zSetOps = this.template.opsForZSet();
        this.setOps = this.template.opsForSet();
        this.hashOps = this.template.opsForHash();
        this.listOps = this.template.opsForList();
        this.pollScript = new PollScript();

        this.objectMapper = objectMapper;
        setDuplicateRemover(this);
    }

    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        hashOps.put(detailKey(task), request.getUrl(), serializerRequest(request));
        zSetOps.add(todoKey(task), request.getUrl(), request.getPriority());
    }

    @Override
    protected void pushWhenNoDuplicate(Collection<Request> requests, Task task) {
        Map<String, String> requestMap = requests.stream()
                .collect(Collectors.toMap(Request::getUrl, this::serializerRequest));
        hashOps.putAll(detailKey(task), requestMap);
        Set<ZSetOperations.TypedTuple<String>> typedTupleList = requests.stream()
                .map(req -> new DefaultTypedTuple<>(req.getUrl(), (double) req.getPriority()))
                .collect(Collectors.toSet());
        zSetOps.add(todoKey(task), typedTupleList);
    }

    @Override
    public Request poll(Task task) {
        String request = template.execute(pollScript, Lists.newArrayList(todoKey(task), detailKey(task)));
        if (request == null) {
            return null;
        }
        return deserializerRequest(request);
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        Long add = setOps.add(visitedKey(task), request.getUrl());
        return add == null || add == 0;
    }

    @Override
    public List<Request> filterDuplicate(List<Request> requests, Task task) {
        List<Object> result = template.executePipelined((RedisCallback<Long>) conn -> {
            for (Request request : requests) {
                conn.sAdd(redisSerializer.serialize(visitedKey(task)),
                        redisSerializer.serialize(request.getUrl()));
            }
            return null;
        });
        ArrayList<Request> filterRequests = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            if (Objects.equals(result.get(i), 1L)) {
                filterRequests.add(requests.get(i));
            }
        }

        return filterRequests;
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

    @Override
    public int getFailCount(Task task) {
        Long count = listOps.size(failKey(task));
        return count == null ? 0 : count.intValue();
    }

    @Override
    public void pushFail(Request request, Task task) {
        listOps.leftPush(failKey(task), serializerRequest(request));
    }

    @Override
    public Request pollFail(Task task) {
        String request = listOps.rightPop(failKey(task));
        if (request == null) {
            return null;
        }
        return deserializerRequest(request);
    }


    protected String serializerRequest(Request request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("serializer request fail:" + request);
        }
    }

    protected Request deserializerRequest(String request) {
        try {
            return objectMapper.readValue(request, Request.class);
        } catch (IOException e) {
            throw new IllegalStateException("deserializer request fail:" + request);
        }
    }

    protected String visitedKey(Task task) {
        return VISITED_QUEUE_KEY + task.getUUID();
    }

    protected String todoKey(Task task) {
        return TODO_QUEUE_KEY + task.getUUID();
    }

    protected String detailKey(Task task) {
        return URL_DETAIL_KEY + task.getUUID();
    }

    protected String failKey(Task task) {
        return FAIL_QUEUE_KEY + task.getUUID();
    }

    private static class PollScript implements RedisScript<String> {

        @Override
        public String getSha1() {
            return "0869a5d8faa52f67588bf37ff94d84f52ebd87da";
        }

        @Override
        public Class<String> getResultType() {
            return String.class;
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
