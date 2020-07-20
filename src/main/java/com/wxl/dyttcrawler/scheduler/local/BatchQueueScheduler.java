package com.wxl.dyttcrawler.scheduler.local;

import com.wxl.dyttcrawler.scheduler.BatchDuplicateRemovedScheduler;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Create by wuxingle on 2020/7/20
 * 批量队列
 * @see us.codecraft.webmagic.scheduler.QueueScheduler
 */
public class BatchQueueScheduler extends BatchDuplicateRemovedScheduler
        implements MonitorableScheduler {

    private BlockingQueue<Request> queue = new LinkedBlockingQueue<>();

    @Override
    public void pushWhenNoDuplicate(Request request, Task task) {
        queue.add(request);
    }

    @Override
    public Request poll(Task task) {
        return queue.poll();
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        return queue.size();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return getDuplicateRemover().getTotalRequestsCount(task);
    }
}

