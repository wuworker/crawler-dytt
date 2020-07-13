package com.wxl.crawlerdytt.config;

import com.wxl.crawlerdytt.web.error.DyttErrorAttributes;
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


}
