package com.wxl.crawlerdytt.handler;

import com.wxl.crawlerdytt.core.DyttUrl;

/**
 * Create by wuxingle on 2020/5/2
 * html页面处理
 */
public interface HtmlHandler {

    /**
     * 是否能够处理
     */
    boolean support(DyttUrl url, String html);

    /**
     * 页面解析
     *
     * @return 处理结果
     */
    Object handle(DyttUrl url, String html);

}
