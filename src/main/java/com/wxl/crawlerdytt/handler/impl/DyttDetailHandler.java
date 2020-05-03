package com.wxl.crawlerdytt.handler.impl;

import com.wxl.crawlerdytt.core.DyttDetail;
import com.wxl.crawlerdytt.core.DyttUrl;
import com.wxl.crawlerdytt.handler.FilteredHandler;
import org.jsoup.nodes.Document;

import java.util.regex.Pattern;

/**
 * Create by wuxingle on 2020/5/2
 * 电影天堂详情处理器
 */
public class DyttDetailHandler extends FilteredHandler<DyttDetail> {

    private Pattern pathPattern = Pattern.compile("/html/gndy/\\w+/\\d+/\\d+\\.html");

    /**
     * 页面解析
     *
     * @return 处理结果
     */
    @Override
    protected DyttDetail handle(DyttUrl url, Document doc) {
        return null;
    }

    /**
     * 是否能够处理
     */
    @Override
    public boolean support(DyttUrl url, String html) {
        String path = url.getPath();
        return pathPattern.matcher(path).matches();
    }
}

