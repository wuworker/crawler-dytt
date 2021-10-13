package com.wxl.dyttcrawler.scheduler.local

import com.wxl.dyttcrawler.scheduler.BatchDuplicateRemovedScheduler
import us.codecraft.webmagic.Request
import us.codecraft.webmagic.Task
import us.codecraft.webmagic.scheduler.MonitorableScheduler
import java.util.concurrent.LinkedBlockingQueue

/**
 * Create by wuxingle on 2021/10/11
 * 批量队列
 * @see us.codecraft.webmagic.scheduler.QueueScheduler
 */
class BatchQueueScheduler : BatchDuplicateRemovedScheduler(), MonitorableScheduler {

    private val queue = LinkedBlockingQueue<Request>()

    override fun pushWhenNoDuplicate(request: Request, task: Task) {
        queue.add(request)
    }

    override fun poll(task: Task): Request? = queue.poll()

    override fun getLeftRequestsCount(task: Task): Int = queue.size

    override fun getTotalRequestsCount(task: Task): Int = duplicateRemover.getTotalRequestsCount(task)

}
