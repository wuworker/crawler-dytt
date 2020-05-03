package com.wxl.crawlerdytt.frontier.redis;

import com.wxl.crawlerdytt.core.DyttUrl;
import com.wxl.crawlerdytt.frontier.Frontier;
import com.wxl.crawlerdytt.utils.IntegerRedisSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Create by wuxingle on 2020/5/1
 * redis zset实现todo表
 */
@Slf4j
public class RedisFrontier implements Frontier {

    private static final String DEFAULT_KEY = "dyttFrontier";

    private RedisScript<Object> script;

    private RedisSerializer<Integer> luaArgsSerializer;

    private RedisSerializer<Object> luaValueSerializer;

    private RedisTemplate<String, Object> template;

    private ZSetOperations<String, Object> zsetOps;

    public RedisFrontier(RedisTemplate<String, Object> template,
                         RedisScript<Object> removeTopScript) {
        this.template = template;
        this.zsetOps = template.opsForZSet();
        this.script = removeTopScript;
        this.luaArgsSerializer = new IntegerRedisSerializer();
        this.luaValueSerializer = (RedisSerializer<Object>) template.getValueSerializer();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DyttUrl> next(int num) {
        if (num <= 0) {
            return Collections.emptyList();
        }
        return (List<DyttUrl>) template.execute(script, luaArgsSerializer, luaValueSerializer,
                Collections.singletonList(DEFAULT_KEY), num - 1);
    }

    @Override
    public void add(DyttUrl... urls) {
        Set<TypedTuple<Object>> sets = new HashSet<>(urls.length, 1);
        for (DyttUrl url : urls) {
            sets.add(new DefaultTypedTuple<>(url, url.getWeight() * 1.0));
        }

        Long l = zsetOps.add(DEFAULT_KEY, sets);
        if (log.isDebugEnabled()) {
            if (l == null || l != urls.length) {
                log.warn("add url fail:{}", l);
            }
        }
    }
}
