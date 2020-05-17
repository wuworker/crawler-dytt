package com.wxl.crawlerdytt.urlhandler;

import com.wxl.crawlerdytt.properties.CrawlerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Create by wuxingle on 2020/5/16
 * 只匹配当前domain的url
 */
@Slf4j
@Component
@EnableConfigurationProperties(CrawlerProperties.class)
public class DyttUrlFilter implements UrlFilter {

    private String domain;

    public DyttUrlFilter(CrawlerProperties crawlerProperties) {
        this.domain = crawlerProperties.getDomain();
    }

    /**
     * 是否匹配
     *
     * @param url 相对路径或者绝对路径
     */
    @Override
    public boolean filter(String url) {
        return !url.startsWith("http") || url.contains(domain);
    }
}
