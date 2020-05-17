package com.wxl.crawlerdytt.urlhandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Create by wuxingle on 2020/5/16
 * url过滤
 */
public interface UrlFilter {


    /**
     * 是否匹配
     *
     * @param url 相对路径或者绝对路径
     */
    boolean filter(String url);


    /**
     * 提取匹配的url
     */
    default List<String> filter(List<String> urls) {
        return urls.stream()
                .filter(this::filter)
                .collect(Collectors.toList());
    }
}
