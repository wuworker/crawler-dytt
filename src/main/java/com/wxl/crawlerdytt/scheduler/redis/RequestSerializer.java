package com.wxl.crawlerdytt.scheduler.redis;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import us.codecraft.webmagic.Request;

/**
 * Create by wuxingle on 2020/5/20
 * request序列化
 */
public class RequestSerializer implements RedisSerializer<Request> {

    private RedisSerializer<Object> jdk = RedisSerializer.java();

    @Override
    public byte[] serialize(Request request) throws SerializationException {
        return jdk.serialize(request);
    }

    @Override
    public Request deserialize(byte[] bytes) throws SerializationException {
        return (Request) jdk.deserialize(bytes);
    }
}
