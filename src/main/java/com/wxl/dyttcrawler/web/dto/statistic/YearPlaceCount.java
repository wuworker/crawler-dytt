package com.wxl.dyttcrawler.web.dto.statistic;

import com.wxl.dyttcrawler.web.dto.Item;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Create by wuxingle on 2020/7/13
 * 年份地区数据
 */
@NoArgsConstructor
public class YearPlaceCount extends Item<String, List<PlaceCount>> {

    private static final long serialVersionUID = 7480660194107013629L;

    public YearPlaceCount(String key, List<PlaceCount> value) {
        super(key, value);
    }
}
