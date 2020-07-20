package com.wxl.dyttcrawler.scheduler;

import com.wxl.dyttcrawler.scheduler.local.BatchHashSetDuplicateRemover;
import org.apache.commons.collections.CollectionUtils;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create by wuxingle on 2020/7/20
 * 批量移除重复request的scheduler
 */
public abstract class BatchDuplicateRemovedScheduler
        extends DuplicateRemovedScheduler
        implements BatchScheduler {

    public BatchDuplicateRemovedScheduler() {
        setDuplicateRemover(new BatchHashSetDuplicateRemover());
    }

    @Override
    public void push(List<Request> requests, Task task) {
        List<Request> shouldPutRequests = new ArrayList<>();
        List<Request> shouldFilterRequests = new ArrayList<>();
        for (Request request : requests) {
            if (shouldReserved(request) || noNeedToRemoveDuplicate(request)) {
                shouldPutRequests.add(request);
            } else {
                shouldFilterRequests.add(request);
            }
        }
        if (CollectionUtils.isNotEmpty(shouldFilterRequests)) {
            shouldFilterRequests = getDuplicateRemover().filterDuplicate(shouldFilterRequests, task);
            shouldPutRequests.addAll(shouldFilterRequests);
        }

        if (CollectionUtils.isNotEmpty(shouldPutRequests)) {
            if (logger.isDebugEnabled()) {
                logger.debug("push to queue {}", shouldPutRequests.stream()
                        .map(Request::getUrl).collect(Collectors.toList()));
            }
            pushWhenNoDuplicate(shouldPutRequests, task);
        }
    }

    @Override
    public BatchDuplicateRemover getDuplicateRemover() {
        return (BatchDuplicateRemover) super.getDuplicateRemover();
    }

    @Override
    public BatchDuplicateRemovedScheduler setDuplicateRemover(DuplicateRemover duplicatedRemover) {
        if (!(duplicatedRemover instanceof BatchDuplicateRemover)) {
            throw new IllegalStateException("DuplicateRemover must is batch impl");
        }
        super.setDuplicateRemover(duplicatedRemover);
        return this;
    }

    protected abstract void pushWhenNoDuplicate(Request request, Task task);

    protected void pushWhenNoDuplicate(Collection<Request> requests, Task task) {
        for (Request request : requests) {
            pushWhenNoDuplicate(request, task);
        }
    }
}
