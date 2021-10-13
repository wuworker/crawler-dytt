package com.wxl.dyttcrawler.scheduler.local

import com.wxl.dyttcrawler.scheduler.BatchDuplicateRemovedScheduler
import us.codecraft.webmagic.Request
import us.codecraft.webmagic.Task
import us.codecraft.webmagic.scheduler.MonitorableScheduler
import us.codecraft.webmagic.utils.NumberUtils
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.PriorityBlockingQueue

/**
 * Create by wuxingle on 2021/10/11
 * 批量优先队列
 * @see us.codecraft.webmagic.scheduler.PriorityScheduler
 */
class BatchPriorityScheduler : BatchDuplicateRemovedScheduler(), MonitorableScheduler {

    companion object {
        const val INITIAL_CAPACITY = 5
    }

    private val noPriorityQueue = LinkedBlockingQueue<Request>()

    private val priorityQueuePlus = PriorityBlockingQueue<Request>(INITIAL_CAPACITY) { r1, r2 ->
        NumberUtils.compareLong(r1.priority, r2.priority)
    }

    private val priorityQueueMinus = PriorityBlockingQueue<Request>(INITIAL_CAPACITY) { r1, r2 ->
        NumberUtils.compareLong(r1.priority, r2.priority)
    }

    override fun pushWhenNoDuplicate(request: Request, task: Task) {
        when {
            request.priority > 0 -> priorityQueuePlus.add(request)
            request.priority < 0 -> priorityQueueMinus.add(request)
            else -> noPriorityQueue.add(request)
        }
    }

    override fun poll(task: Task): Request? {
        var req = priorityQueuePlus.poll()
        if (req != null) {
            return req
        }
        req = noPriorityQueue.poll()
        if (req != null) {
            return req
        }
        return priorityQueueMinus.poll()
    }

    override fun getLeftRequestsCount(task: Task): Int = noPriorityQueue.size

    override fun getTotalRequestsCount(task: Task): Int = duplicateRemover.getTotalRequestsCount(task)
}

