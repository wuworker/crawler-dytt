package com.wxl.crawlerdytt.handler.impl;

import com.wxl.crawlerdytt.core.DyttUrl;
import com.wxl.crawlerdytt.handler.FilteredHandler;
import org.jsoup.nodes.Document;

import java.util.List;

/**
 * Create by wuxingle on 2020/5/2
 * 页面链接提取
 */
public class DyttLinkExtractHandler extends FilteredHandler<List<DyttUrl>> {


    /**
     * 页面解析
     *
     * @return 处理结果
     */
    @Override
    protected List<DyttUrl> handle(DyttUrl url, Document doc) {
        return null;
    }


    /**
     * 是否能够处理
     */
    @Override
    public boolean support(DyttUrl url, String html) {
        return true;
    }


}

