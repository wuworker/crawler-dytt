package com.wxl.crawlerdytt.frontier.redis;

import com.google.common.io.CharStreams;
import com.wxl.crawlerdytt.properties.FrontierProperties;
import lombok.extern.slf4j.Slf4j;
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
 * redis配置
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(FrontierProperties.class)
public class RedisFrontierConfig {

    private final ResourceLoader resourceLoader;

    private final FrontierProperties frontierProperties;

    @Autowired
    public RedisFrontierConfig(ResourceLoader resourceLoader,
                               FrontierProperties frontierProperties) {
        this.resourceLoader = resourceLoader;
        this.frontierProperties = frontierProperties;
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

    @Bean(RedisFrontier.REMOVE_TOP_SCRIPT_BEAN_NAME)
    public RedisScript<Object> removeTopScript() throws IOException {
        String path = frontierProperties.getRemoveTopScript();
        String scriptSha1 = frontierProperties.getScriptSha1();
        String script;
        try (InputStream in = resourceLoader.getResource(path).getInputStream()) {
            script = CharStreams.toString(new InputStreamReader(in, StandardCharsets.UTF_8));
        }


        return new RedisScript<Object>() {
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
    }


}
