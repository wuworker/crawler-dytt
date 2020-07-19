package com.wxl.dyttcrawler.urlhandler;

import us.codecraft.webmagic.Page;

import java.net.URL;

/**
 * Create by wuxingle on 2020/5/10
 * 链接优先级计算
 */
public interface PriorityUrlCalculator {

    /**
     * 计算url优先级
     */
    int calculate(Page page, URL url);

}
