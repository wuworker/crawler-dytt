package com.wxl.crawlerdytt.handler;

import com.wxl.crawlerdytt.core.DyttUrl;

/**
 * Create by wuxingle on 2020/5/2
 * 结果处理器
 */
public interface HtmlResultHandler {

    /**
     * 是否能够处理
     */
    boolean support(Object result);

    /**
     * 结果处理
     */
    void handle(DyttUrl url, Object result);

}
