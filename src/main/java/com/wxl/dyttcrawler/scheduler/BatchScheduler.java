package com.wxl.dyttcrawler.scheduler;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.Scheduler;

import java.util.List;

/**
 * Create by wuxingle on 2020/7/20
 * 批量队列
 */
public interface BatchScheduler extends Scheduler {

    /**
     * 批量放入
     */
    default void push(List<Request> requests, Task task) {
        for (Request request : requests) {
            push(request, task);
        }
    }

}
