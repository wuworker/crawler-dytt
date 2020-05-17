package com.wxl.crawlerdytt.urlhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;

import java.util.regex.Pattern;

/**
 * Create by wuxingle on 2020/5/10
 * 详情页优先
 */
@Slf4j
@Component
public class DyttPriorityUrlCalculator implements PriorityUrlCalculator {

    private static final Pattern DETAIL_PATH_PATTERN = Pattern.compile("/html/gndy/\\w+/(\\d+)/(\\d+)\\.html");

    @Override
    public int calculate(Page page, String url) {
        if (DETAIL_PATH_PATTERN.matcher(url).find()) {
            return 10;
        }
        return 0;
    }


}
