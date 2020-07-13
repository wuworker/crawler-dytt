package com.wxl.crawlerdytt.web.dto.statistic;

import com.wxl.crawlerdytt.web.dto.AggTwoResult;

/**
 * Create by wuxingle on 2020/7/13
 * 年-月份数统计
 */
public class YearMonthCount extends AggTwoResult<String, Integer, Integer> {

    private static final long serialVersionUID = 1489186609617050423L;

    /**
     * 新增月份数据
     */
    public void add(String key, MonthCount monthCount) {
        super.add(key, monthCount);
    }
}
