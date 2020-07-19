package com.wxl.dyttcrawler.core;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

/**
 * Create by wuxingle on 2020/7/19
 * 请求处理监听
 */
public interface CrawlerListener {

    /**
     * 成功
     */
    void onSuccess(Request request, Task task);

    /**
     * 失败
     */
    void onError(Request request, Task task);
}
