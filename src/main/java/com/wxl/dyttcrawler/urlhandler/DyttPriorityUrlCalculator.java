package com.wxl.dyttcrawler.urlhandler;

import com.wxl.dyttcrawler.core.DyttConstants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;

import java.net.URL;

import static com.wxl.dyttcrawler.core.DyttConstants.*;

/**
 * Create by wuxingle on 2020/5/10
 * url优先级计算
 * 详情页优先
 */
@Slf4j
@Data
@Component
public class DyttPriorityUrlCalculator implements PriorityUrlCalculator {

    /**
     * 链接相关度权重
     */
    private float correlationWeight = 0.7f;

    /**
     * 深度权重
     */
    private float depthWeight = 0.3f;


    @Override
    public int calculate(Page page, URL url) {
        int correlation = (int) (correlationWeight * correlation(url));
        int depth = (int) (depthWeight * depth(page));

        int sum = correlation + depth;
        log.trace("url priority result:{}, correlation:{}", sum, correlation);
        return sum;
    }

    /**
     * 链接相关度计算
     */
    protected int correlation(URL url) {
        String path = url.getPath();
        // 首页
        if (INDEX_PATTERN.matcher(path).matches()) {
            return 100;
        }
        // 详情页
        if (GNDY_DETAIL_PATH_PATTERN.matcher(path).matches()) {
            return 90;
        }
        // 列表页
        if (GNDY_INDEX_PATTERN.matcher(path).matches()
                || CATEGORY_LIST_PATH_PATTERN.matcher(path).matches()) {
            return 70;
        }
        // 其他页面
        return 30;
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
}
