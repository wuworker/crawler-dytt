package com.wxl.crawlerdytt.processor;


import us.codecraft.webmagic.Page;

/**
 * Create by wuxingle on 2020/5/10
 * 页面匹配处理
 */
public interface DyttProcessor {

    void process(Page page);

    boolean match(Page page);
}


