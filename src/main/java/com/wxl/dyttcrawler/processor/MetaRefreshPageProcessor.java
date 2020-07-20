package com.wxl.dyttcrawler.processor;

import com.wxl.dyttcrawler.urlhandler.PriorityUrlCalculator;
import com.wxl.dyttcrawler.urlhandler.UrlFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by wuxingle on 2020/7/18
 * 处理浏览器自动刷新的标签
 * <head>
 * <meta http-equiv="refresh" content="0;URL=index.htm">
 * </head>
 */
@Slf4j
@Order(-100)
@Component
public class MetaRefreshPageProcessor extends AbstractDyttProcessor {

    private static final Pattern LOCATION_PATTERN = Pattern.compile("URL=(.+)");

    @Autowired
    public MetaRefreshPageProcessor(PriorityUrlCalculator priorityCalculator,
                                    UrlFilter urlFilter) {
        super(priorityCalculator, urlFilter);
    }

    @Override
    public void process(Page page) {
        log.debug("process refresh meta:{}", page.getUrl());
        Element refreshMeta = getRefreshMeta(page);
        String content = refreshMeta.attr("content");
        if (StringUtils.isNotBlank(content)) {
            Matcher matcher = LOCATION_PATTERN.matcher(content);
            if (matcher.find()) {
                String location = matcher.group(1);
                addLink(page, location);
            }
        }
    }

    @Override
    public boolean match(Page page) {
        return getRefreshMeta(page) != null;
    }

    private Element getRefreshMeta(Page page) {
        Element head = page.getHtml().getDocument().head();
        return head.select("meta[http-equiv=refresh]").first();
    }
}
