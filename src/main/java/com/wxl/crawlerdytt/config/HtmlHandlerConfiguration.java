package com.wxl.crawlerdytt.config;

import com.wxl.crawlerdytt.core.DyttDetail;
import com.wxl.crawlerdytt.core.DyttUrl;
import com.wxl.crawlerdytt.handler.DyttFilter;
import com.wxl.crawlerdytt.handler.FilteredHandler;
import com.wxl.crawlerdytt.handler.impl.DyttDetailHandler;
import com.wxl.crawlerdytt.handler.impl.DyttDetailResultHandler;
import com.wxl.crawlerdytt.handler.impl.DyttLinkExtractHandler;
import com.wxl.crawlerdytt.handler.impl.DyttLinkResultHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * Create by wuxingle on 2020/5/2
 * 处理器配置
 */
@Slf4j
@Configuration
public class HtmlHandlerConfiguration {

    /**
     * 页面提取url处理
     */
    @Order(Ordered.LOWEST_PRECEDENCE)
    @Bean
    public DyttLinkExtractHandler dyttLinkExtractHandler(ObjectProvider<DyttFilter<List<DyttUrl>>> filters) {
        DyttLinkExtractHandler dyttLinkHandler = new DyttLinkExtractHandler();
        addFilter(dyttLinkHandler, filters);
        return dyttLinkHandler;
    }


    /**
     * 详情页处理
     */
    @Order(0)
    @Bean
    public DyttDetailHandler dyttDetailHandler(ObjectProvider<DyttFilter<DyttDetail>> filters) {
        DyttDetailHandler dyttDetailHandler = new DyttDetailHandler();
        addFilter(dyttDetailHandler, filters);
        return dyttDetailHandler;
    }

    /**
     * url结果处理
     */
    @Bean
    public DyttLinkResultHandler dyttLinkResultHandler() {
        return new DyttLinkResultHandler();
    }

    /**
     * 详情页结果处理
     */
    @Bean
    public DyttDetailResultHandler dyttDetailResultHandler() {
        return new DyttDetailResultHandler();
    }


    private <T> void addFilter(FilteredHandler<T> handler, ObjectProvider<DyttFilter<T>> filters) {
        filters.orderedStream().forEach(handler::addFilter);
    }


}
