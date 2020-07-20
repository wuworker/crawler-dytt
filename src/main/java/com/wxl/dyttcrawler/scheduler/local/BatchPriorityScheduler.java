package com.wxl.dyttcrawler.scheduler.local;

import com.wxl.dyttcrawler.scheduler.BatchDuplicateRemovedScheduler;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.utils.NumberUtils;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Create by wuxingle on 2020/7/20
 * 批量优先队列
 *
 * @see us.codecraft.webmagic.scheduler.PriorityScheduler
 */
public class BatchPriorityScheduler extends BatchDuplicateRemovedScheduler
        implements MonitorableScheduler {

    private static final int INITIAL_CAPACITY = 5;

    private BlockingQueue<Request> noPriorityQueue = new LinkedBlockingQueue<>();

    private PriorityBlockingQueue<Request> priorityQueuePlus = new PriorityBlockingQueue<>(
            INITIAL_CAPACITY, (o1, o2) -> -NumberUtils.compareLong(o1.getPriority(), o2.getPriority()));

    private PriorityBlockingQueue<Request> priorityQueueMinus = new PriorityBlockingQueue<>(
            INITIAL_CAPACITY, (o1, o2) -> -NumberUtils.compareLong(o1.getPriority(), o2.getPriority()));

    @Override
    public void pushWhenNoDuplicate(Request request, Task task) {
        if (request.getPriority() == 0) {
            noPriorityQueue.add(request);
        } else if (request.getPriority() > 0) {
            priorityQueuePlus.put(request);
        } else {
            priorityQueueMinus.put(request);
        }
    }

    @Override
    public synchronized Request poll(Task task) {
        Request poll = priorityQueuePlus.poll();
        if (poll != null) {
            return poll;
        }
        poll = noPriorityQueue.poll();
        if (poll != null) {
            return poll;
        }
        return priorityQueueMinus.poll();
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        return noPriorityQueue.size();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return getDuplicateRemover().getTotalRequestsCount(task);
    }
}
