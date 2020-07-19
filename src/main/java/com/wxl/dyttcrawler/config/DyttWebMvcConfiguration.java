package com.wxl.dyttcrawler.config;

import com.wxl.dyttcrawler.web.dto.AggOneResult;
import com.wxl.dyttcrawler.web.dto.serializer.AggResultSerializer;
import com.wxl.dyttcrawler.web.error.DyttErrorAttributes;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Create by wuxingle on 2020/7/12
 * web配置
 */
@Configuration
public class DyttWebMvcConfiguration {


    @Bean
    public DefaultErrorAttributes errorAttributes() {
        return new DyttErrorAttributes();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonSerializerCustomizer() {
        return builder -> {
            builder.serializerByType(AggOneResult.class, new AggResultSerializer());
        };
    }


}
