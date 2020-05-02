package com.wxl.crawlerdytt.utils;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;

/**
 * Create by wuxingle on 2020/5/2
 * int类型序列化
 */
public class IntegerRedisSerializer implements RedisSerializer<Integer> {

    @Override
    public byte[] serialize(Integer num) throws SerializationException {
        return num.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Integer deserialize(byte[] bytes) throws SerializationException {
        return Integer.parseInt(new String(bytes, StandardCharsets.UTF_8));
    }
}
