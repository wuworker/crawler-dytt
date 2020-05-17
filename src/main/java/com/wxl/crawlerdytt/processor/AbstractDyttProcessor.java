package com.wxl.crawlerdytt.processor;

import com.wxl.crawlerdytt.urlhandler.PriorityUrlCalculator;
import com.wxl.crawlerdytt.urlhandler.UrlFilter;
import us.codecraft.webmagic.Page;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create by wuxingle on 2020/5/16
 * 包含Link相关处理
 */
public abstract class AbstractDyttProcessor implements DyttProcessor {

    /**
     * url优先级计算
     */
    private PriorityUrlCalculator priorityCalculator;

    /**
     * url过滤器
     */
    private UrlFilter urlFilter;

    public AbstractDyttProcessor(PriorityUrlCalculator priorityCalculator,
                                 UrlFilter urlFilter) {
        this.priorityCalculator = priorityCalculator;
        this.urlFilter = urlFilter;
    }


    /**
     * 添加link
     */
    public void addLinks(Page page) {
        List<String> all = page.getHtml().links().all();
        if (urlFilter != null) {
            all = urlFilter.filter(all);
        }
        all.stream().collect(Collectors.groupingBy(url -> priorityCalculator.calculate(page, url)))
                .forEach((k, v) -> page.addTargetRequests(v, k));

    }

    /**
     * 添加link
     */
    public void addLink(Page page, String url) {
        if (urlFilter == null || urlFilter.filter(url)) {
            int priority = priorityCalculator.calculate(page, url);
            page.addTargetRequests(Collections.singletonList(url), priority);
        }
    }

    /**
     * 存结果对象
     */
    public void putObject(Page page, Object obj) {
        if (obj != null) {
            page.putField(obj.getClass().getName(), obj);
        }
    }
}
