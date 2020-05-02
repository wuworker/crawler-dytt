package com.wxl.crawlerdytt.handler.impl;

import com.wxl.crawlerdytt.core.DyttDetail;
import com.wxl.crawlerdytt.core.DyttUrl;
import com.wxl.crawlerdytt.handler.FilteredHandler;
import org.jsoup.nodes.Document;

/**
 * Create by wuxingle on 2020/5/2
 * 电影天堂详情处理器
 */
public class DyttDetailHandler extends FilteredHandler<DyttDetail> {

    @Override
    protected DyttDetail handle(DyttUrl url, Document doc) {
        return null;
    }
}
