package com.wxl.dyttcrawler.core

import us.codecraft.webmagic.Request
import us.codecraft.webmagic.Task

/**
 * Create by wuxingle on 2021/10/08
 * 请求处理监听
 */
interface CrawlerListener {

    /**
     * 成功处理
     */
    fun onSuccess(request: Request, task: Task)

    /**
     * 失败处理
     */
    fun onError(request: Request, task: Task, e: Exception?)
}
