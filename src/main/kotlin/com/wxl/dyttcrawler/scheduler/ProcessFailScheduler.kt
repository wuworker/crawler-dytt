package com.wxl.dyttcrawler.scheduler

import com.wxl.dyttcrawler.core.CrawlerListener
import us.codecraft.webmagic.Request
import us.codecraft.webmagic.Task

/**
 * Create by wuxingle on 2021/10/11
 * 处理失败的队列
 */
interface ProcessFailScheduler : CrawlerListener {

    /**
     * 放入失败队列
     */
    fun pushFail(request: Request, task: Task)

    /**
     * 拿出失败请求
     */
    fun pollFail(task: Task): Request?

    /**
     * 总数
     */
    fun getFailCount(task: Task): Int

    /**
     * 成功处理
     */
    override fun onSuccess(request: Request, task: Task) {
    }

    /**
     * 失败处理
     */
    override fun onError(request: Request, task: Task, e: Exception?) {
        pushFail(request, task)
    }

}