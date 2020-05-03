package com.wxl.crawlerdytt.handler.impl;

import com.wxl.crawlerdytt.core.DyttDetail;
import com.wxl.crawlerdytt.core.DyttUrl;
import com.wxl.crawlerdytt.handler.HtmlResultHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Create by wuxingle on 2020/5/2
 * 详情处理
 */
@Slf4j
public class DyttDetailResultHandler implements HtmlResultHandler {

    /**
     * 是否能够处理
     */
    @Override
    public boolean support(Object result) {
        return result instanceof DyttDetail;
    }

    /**
     * 结果处理
     */
    @Override
    public void handle(DyttUrl url, Object result) {

    }
}
