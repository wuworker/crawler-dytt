package com.wxl.crawlerdytt.handler.impl;

import com.wxl.crawlerdytt.core.DyttUrl;
import com.wxl.crawlerdytt.handler.FilteredHandler;
import org.jsoup.nodes.Document;

import java.util.List;

/**
 * Create by wuxingle on 2020/5/2
 * 电影天堂url处理器
 */
public class DyttUrlHandler extends FilteredHandler<List<DyttUrl>> {


    @Override
    protected List<DyttUrl> handle(DyttUrl url, Document doc) {
        return null;
    }
}

