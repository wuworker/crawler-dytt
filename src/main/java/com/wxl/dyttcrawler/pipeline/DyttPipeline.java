package com.wxl.dyttcrawler.pipeline;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * Create by wuxingle on 2020/5/10
 * 对象匹配pipeline
 */
public abstract class DyttPipeline<T> implements Pipeline {

    private Class<T> clazz;

    protected DyttPipeline(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        Object obj = resultItems.get(clazz.getName());
        if (obj != null) {
            process((T) obj, resultItems, task);
        }
    }

    protected abstract void process(T obj, ResultItems resultItems, Task task);
}


