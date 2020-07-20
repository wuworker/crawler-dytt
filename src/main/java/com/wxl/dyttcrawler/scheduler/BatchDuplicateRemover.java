package com.wxl.dyttcrawler.scheduler;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Create by wuxingle on 2020/7/20
 * 批量重复移除
 */
public interface BatchDuplicateRemover extends DuplicateRemover {

    /**
     * 过滤掉重复的
     *
     * @return 不重复的
     */
    default List<Request> filterDuplicate(List<Request> requests, Task task) {
        return requests.stream().filter(req -> !isDuplicate(req, task))
                .collect(Collectors.toList());
    }
}
