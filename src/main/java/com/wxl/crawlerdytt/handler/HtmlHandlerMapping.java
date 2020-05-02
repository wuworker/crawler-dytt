package com.wxl.crawlerdytt.handler;

import com.wxl.crawlerdytt.core.DyttUrl;

/**
 * Create by wuxingle on 2020/5/2
 * html处理器匹配
 */
public interface HtmlHandlerMapping {


    HtmlHandler getHandler(DyttUrl url, String html);

}
