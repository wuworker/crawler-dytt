package com.wxl.dyttcrawler.scheduler

import com.wxl.dyttcrawler.scheduler.local.BatchHashSetDuplicateRemover
import us.codecraft.webmagic.Request
import us.codecraft.webmagic.Task
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler
import us.codecraft.webmagic.scheduler.component.DuplicateRemover

/**
 * Create by wuxingle on 2021/10/11
 * 批量移除重复request的scheduler
 */
abstract class BatchDuplicateRemovedScheduler : DuplicateRemovedScheduler(), BatchScheduler {

    init {
        duplicateRemover = BatchHashSetDuplicateRemover()
    }

    override fun push(requests: List<Request>, task: Task) {
        val shouldPutRequests = mutableListOf<Request>()
        val shouldFilterRequests = mutableListOf<Request>()

        for (request in requests) {
            if (shouldReserved(request) || noNeedToRemoveDuplicate(request)) {
                shouldPutRequests.add(request)
            } else {
                shouldFilterRequests.add(request)
            }
        }
        if (shouldFilterRequests.isNotEmpty()) {
            shouldPutRequests.addAll(duplicateRemover.filterDuplicate(shouldFilterRequests, task))
        }

        if (shouldPutRequests.isNotEmpty()) {
            if (logger.isDebugEnabled) {
                logger.debug("push to queue {}", shouldPutRequests.map(Request::getUrl))
            }
            pushWhenNoDuplicate(shouldPutRequests, task)
        }
    }

    override fun getDuplicateRemover(): BatchDuplicateRemover =
        super.getDuplicateRemover() as BatchDuplicateRemover


    final override fun setDuplicateRemover(duplicatedRemover: DuplicateRemover): BatchDuplicateRemovedScheduler {
        if (duplicatedRemover !is BatchDuplicateRemover) {
            throw IllegalStateException("DuplicateRemover must is batch impl")
        }
        super.setDuplicateRemover(duplicatedRemover)
        return this
    }

    abstract override fun pushWhenNoDuplicate(request: Request, task: Task)

    open fun pushWhenNoDuplicate(requests: Collection<Request>, task: Task) {
        requests.forEach { pushWhenNoDuplicate(it, task) }
    }
}
