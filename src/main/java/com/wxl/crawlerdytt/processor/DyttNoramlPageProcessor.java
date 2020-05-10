package com.wxl.crawlerdytt.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;

import java.util.List;

/**
 * Create by wuxingle on 2020/5/10
 * 普通页处理,提取link
 */
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
@Component
public class DyttNoramlPageProcessor implements DyttProcessor {

    @Override
    public void process(Page page) {
        List<String> all = page.getHtml().links().all();
        for (String url : all) {
            page.addTargetRequest(url);
        }
    }

    @Override
    public boolean match(Page page) {
        return true;
    }
}
