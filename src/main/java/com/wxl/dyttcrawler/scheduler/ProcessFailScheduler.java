package com.wxl.dyttcrawler.scheduler;

import com.wxl.dyttcrawler.core.CrawlerListener;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

/**
 * Create by wuxingle on 2020/7/19
 * 处理失败的队列
 */
public interface ProcessFailScheduler extends CrawlerListener {

    /**
     * 放入失败队列
     */
    void pushFail(Request request, Task task);

    /**
     * 拿出失败请求
     */
    Request pollFail(Task task);


    /**
     * 总数
     */
    int getFailCount(Task task);

    /**
     * 成功
     */
    @Override
    default void onSuccess(Request request, Task task) {

    }

    /**
     * 失败
     */
    @Override
    default void onError(Request request, Task task) {
        pushFail(request, task);
    }

}
