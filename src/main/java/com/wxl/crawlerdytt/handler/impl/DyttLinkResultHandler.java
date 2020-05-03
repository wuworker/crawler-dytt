package com.wxl.crawlerdytt.handler.impl;

import com.wxl.crawlerdytt.core.DyttUrl;
import com.wxl.crawlerdytt.handler.HtmlResultHandler;

/**
 * Create by wuxingle on 2020/5/2
 * 提取的链接处理
 */
public class DyttLinkResultHandler implements HtmlResultHandler {

    /**
     * 是否能够处理
     */
    @Override
    public boolean support(Object result) {
        return false;
    }

    /**
     * 结果处理
     */
    @Override
    public void handle(DyttUrl url, Object result) {

    }
}
