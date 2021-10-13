package com.wxl.dyttcrawler.scheduler

import us.codecraft.webmagic.Request
import us.codecraft.webmagic.Task
import us.codecraft.webmagic.scheduler.Scheduler

/**
 * Create by wuxingle on 2021/10/11
 * 批量队列
 */
interface BatchScheduler : Scheduler {

    /**
     * 批量放入
     */
    fun push(requests: List<Request>, task: Task) {
        for (request in requests) {
            push(request, task)
        }
    }

}
