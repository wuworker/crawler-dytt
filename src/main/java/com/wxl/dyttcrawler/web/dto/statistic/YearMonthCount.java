package com.wxl.dyttcrawler.web.dto.statistic;

import com.wxl.dyttcrawler.web.dto.Item;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Create by wuxingle on 2020/7/13
 * 年-月份数统计
 */
@NoArgsConstructor
public class YearMonthCount extends Item<Integer, List<MonthCount>> {

    private static final long serialVersionUID = 1489186609617050423L;

    public YearMonthCount(Integer key, List<MonthCount> value) {
        super(key, value);
    }
}
