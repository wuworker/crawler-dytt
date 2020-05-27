package com.wxl.crawlerdytt.core;

/**
 * Create by wuxingle on 2020/5/27
 * 爬虫管理
 */
public interface CrawlerManager {

    enum Status {
        // 空闲
        IDLE,
        // 运行中
        RUNNING,
        // 停止中
        SHUTDOWN
    }

    /**
     * 启动
     */
    boolean start();

    /**
     * 停止
     */
    boolean shutdown();

    /**
     * 当前状态
     */
    Status getStatus();

}
