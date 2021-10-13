package com.wxl.dyttcrawler.config

import com.wxl.dyttcrawler.web.error.DyttErrorAttributes
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Create by wuxingle on 2021/10/11
 * web配置
 */
@Configuration
class DyttWebMvcConfiguration {

    @Bean
    fun errorAttributes(): DefaultErrorAttributes {
        return DyttErrorAttributes()
    }

    @Bean
    fun jacksonSerializerCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
        return Jackson2ObjectMapperBuilderCustomizer {
        }
    }
}
