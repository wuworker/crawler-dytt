package com.wxl.dyttcrawler.core;

import java.util.concurrent.Future;

/**
 * Create by wuxingle on 2020/6/26
 * 爬虫线程池
 */
public interface ThreadPool {

    /**
     * 任务提交
     * 阻塞到提交成功
     */
    Future<?> execute(Runnable runnable) throws InterruptedException;

    /**
     * 关闭
     * 阻塞到关闭结束
     */
    void shutdown() throws InterruptedException;

    /**
     * 是否关闭
     */
    boolean isShutDown();


    /**
     * 运行中线程
     */
    int getActiveThreadNum();

    /**
     * 总线程
     */
    int getMaxThreadNum();
}
