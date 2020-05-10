package com.wxl.crawlerdytt.priority;

import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.utils.UrlUtils;

/**
 * Create by wuxingle on 2020/5/10
 * 链接优先级计算
 */
public interface PriorityUrlCalculator {

    /**
     * 计算url优先级
     */
    int calculate(Page page, String url);


    default void calculateAndAdd(Page page, String url) {
        if (!StringUtils.hasText(url) || url.equals("#") || url.startsWith("javascript")) {
            return;
        }
        url = UrlUtils.canonicalizeUrl(url, page.getUrl().toString());
        Request request = new Request(url);
        request.setPriority(calculate(page, url));
        page.addTargetRequest(request);
    }
}
