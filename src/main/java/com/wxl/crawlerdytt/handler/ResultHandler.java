package com.wxl.crawlerdytt.handler;

import com.wxl.crawlerdytt.core.DyttUrl;

/**
 * Create by wuxingle on 2020/5/2
 * 结果处理器
 */
public interface ResultHandler {


    void handle(DyttUrl url, Object result);

}
