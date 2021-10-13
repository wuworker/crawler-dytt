package com.wxl.dyttcrawler.scheduler

import us.codecraft.webmagic.Request
import us.codecraft.webmagic.Task
import us.codecraft.webmagic.scheduler.component.DuplicateRemover

/**
 * Create by wuxingle on 2021/10/11
 * 批量重复移除
 */
interface BatchDuplicateRemover : DuplicateRemover {

    /**
     * 过滤掉重复的
     *
     * @return 不重复的
     */
    fun filterDuplicate(requests: List<Request>, task: Task): List<Request> =
        requests.filterNot { isDuplicate(it, task) }

}
