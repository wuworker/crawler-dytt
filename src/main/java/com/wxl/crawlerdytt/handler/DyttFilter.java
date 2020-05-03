package com.wxl.crawlerdytt.handler;

/**
 * Create by wuxingle on 2020/5/2
 * 处理结果过滤器
 */
public interface DyttFilter<T> {


    T filter(T data);

}
