package com.wxl.crawlerdytt.handler;

/**
 * Create by wuxingle on 2020/5/2
 * 结果处理器匹配
 */
public interface ResultHandlerMapping {


    ResultHandler getResultHandler(Object result);


}
