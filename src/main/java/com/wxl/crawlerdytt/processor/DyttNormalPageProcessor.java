package com.wxl.crawlerdytt.processor;

import com.wxl.crawlerdytt.urlhandler.PriorityUrlCalculator;
import com.wxl.crawlerdytt.urlhandler.UrlFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;

/**
 * Create by wuxingle on 2020/5/10
 * 普通页处理,提取link
 */
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
@Component
public class DyttNormalPageProcessor extends AbstractDyttProcessor {


    @Autowired
    public DyttNormalPageProcessor(PriorityUrlCalculator priorityCalculator,
                                   UrlFilter urlFilter) {
        super(priorityCalculator, urlFilter);
    }

    @Override
    public void process(Page page) {
        addLinks(page);
    }

    @Override
    public boolean match(Page page) {
        return true;
    }
}
