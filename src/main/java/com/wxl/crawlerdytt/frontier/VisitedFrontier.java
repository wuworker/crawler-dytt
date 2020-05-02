package com.wxl.crawlerdytt.frontier;

import com.wxl.crawlerdytt.core.DyttUrl;

/**
 * Create by wuxingle on 2020/5/1
 * visited表
 */
public interface VisitedFrontier {

    void add(DyttUrl url);

    boolean contains(DyttUrl url);
}
