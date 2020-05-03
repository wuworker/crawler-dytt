package com.wxl.crawlerdytt.config;

import com.google.common.io.CharStreams;
import com.wxl.crawlerdytt.frontier.local.BloomVisitedFrontier;
import com.wxl.crawlerdytt.frontier.redis.RedisFrontier;
import com.wxl.crawlerdytt.properties.FrontierProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Create by wuxingle on 2020/5/2
 * 队列配置
 */
@Configuration
@EnableConfigurationProperties(FrontierProperties.class)
public class FrontierConfiguration {

    private final ResourceLoader resourceLoader;

    private final FrontierProperties frontierProperties;

    @Autowired
    public FrontierConfiguration(ResourceLoader resourceLoader,
                                 FrontierProperties frontierProperties) {
        this.resourceLoader = resourceLoader;
        this.frontierProperties = frontierProperties;
    }

    /**
     * visited队列
     */
    @Bean
    public BloomVisitedFrontier bloomVisitedFrontier() {
        return new BloomVisitedFrontier();
    }

    /**
     * todo队列
     */
    @Bean
    public RedisFrontier redisFrontier(RedisTemplate<String, Object> redisTemplate) throws IOException {
        String path = frontierProperties.getRemoveTopScript();
        String scriptSha1 = frontierProperties.getScriptSha1();
        String script;
        try (InputStream in = resourceLoader.getResource(path).getInputStream()) {
            script = CharStreams.toString(new InputStreamReader(in, StandardCharsets.UTF_8));
        }

        RedisScript<Object> removeTopScript = new RedisScript<Object>() {
            @Override
            public String getSha1() {
                return scriptSha1;
            }

            @Override
            public Class<Object> getResultType() {
                return Object.class;
            }

            @Override
            public String getScriptAsString() {
                return script;
            }
        };
        return new RedisFrontier(redisTemplate, removeTopScript);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer();

        template.setConnectionFactory(connectionFactory);
        template.setStringSerializer(stringSerializer);
        template.setValueSerializer(jdkSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jdkSerializer);
        return template;
    }

}
