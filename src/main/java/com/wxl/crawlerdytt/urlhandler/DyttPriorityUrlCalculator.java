package com.wxl.crawlerdytt.urlhandler;

import com.wxl.crawlerdytt.core.DyttConstants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by wuxingle on 2020/5/10
 * url优先级计算
 * 详情页优先
 */
@Slf4j
@Data
@Component
public class DyttPriorityUrlCalculator implements PriorityUrlCalculator {

    private static final Pattern DETAIL_PATH_PATTERN =
            Pattern.compile("/html/gndy/\\w+/(\\d{4})(\\d{2})(\\d{2})/(\\d+)\\.html");


    /**
     * 链接相关度权重
     */
    private int correlationWeight = 3;

    /**
     * 深度权重
     */
    private int depthWeight = 1;

    /**
     * 日期权重
     */
    private int dateWeight = 2;


    @Override
    public int calculate(Page page, String url) {
        int correlation = correlationWeight * correlation(url);
        int depth = depthWeight * depth(page);
        int date = dateWeight * date(url);

        log.trace("url priority, correlation:{},depth:{},date:{}", correlation, depth, date);
        return correlation + depth + date;
    }

    /**
     * 链接相关度计算
     */
    protected int correlation(String url) {
        if (DETAIL_PATH_PATTERN.matcher(url).find()) {
            return 100;
        }
        return 50;
    }

    /**
     * 深度计算
     */
    protected int depth(Page page) {
        Integer depth = (Integer) page.getRequest().getExtra(DyttConstants.RequestAttr.DEPTH);
        if (depth == null) {
            return 100;
        } else if (depth < 10) {
            return (10 - depth) * 10;
        } else {
            return 0;
        }
    }

    /**
     * 日期权重
     */
    protected int date(String url) {
        Matcher matcher = DETAIL_PATH_PATTERN.matcher(url);
        boolean find = matcher.find();
        if (find) {
            int year = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));

            return year % 2000 * 5 + month / 3;
        }
        return 0;
    }
}
