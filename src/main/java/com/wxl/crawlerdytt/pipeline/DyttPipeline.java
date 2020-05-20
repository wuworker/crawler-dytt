package com.wxl.crawlerdytt.pipeline;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * Create by wuxingle on 2020/5/10
 * 对象匹配pipeline
 */
public abstract class DyttPipeline<T> implements Pipeline, ApplicationContextAware {

    private Class<T> clazz;

    protected ApplicationContext applicationContext;

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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    protected abstract void process(T obj, ResultItems resultItems, Task task);
}


