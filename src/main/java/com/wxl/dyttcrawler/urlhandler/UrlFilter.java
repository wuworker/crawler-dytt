package com.wxl.dyttcrawler.urlhandler;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create by wuxingle on 2020/5/16
 * url过滤
 */
public interface UrlFilter {


    /**
     * 是否匹配
     */
    boolean filter(URL url);


    /**
     * 提取匹配的url
     */
    default List<URL> filter(List<URL> urls) {
        return urls.stream()
                .filter(this::filter)
                .collect(Collectors.toList());
    }
}
