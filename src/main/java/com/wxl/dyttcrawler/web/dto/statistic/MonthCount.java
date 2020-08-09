package com.wxl.dyttcrawler.web.dto.statistic;

import com.wxl.dyttcrawler.web.dto.Item;
import lombok.NoArgsConstructor;

/**
 * Create by wuxingle on 2020/7/13
 * 月份数统计
 */
@NoArgsConstructor
public class MonthCount extends Item<Integer, Long> {

    private static final long serialVersionUID = 5812310689332154120L;

    public MonthCount(Integer key, Long value) {
        super(key, value);
    }
}
