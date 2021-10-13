package com.wxl.dyttcrawler.core

import java.util.concurrent.Future

/**
 * Create by wuxingle on 2021/10/08
 * 爬虫线程池
 */
interface ThreadPool {

    /**
     * 是否关闭
     */
    val isShutDown: Boolean

    /**
     * 运行中线程数
     */
    val activeThreadNum: Int

    /**
     * 总线程数
     */
    val maxThreadNum: Int

    /**
     * 任务提交
     * 阻塞到提交成功
     */
    @Throws(InterruptedException::class)
    fun execute(runnable: Runnable): Future<*>

    /**
     * 关闭
     * 阻塞到关闭结束
     */
    @Throws(InterruptedException::class)
    fun shutdown()
}