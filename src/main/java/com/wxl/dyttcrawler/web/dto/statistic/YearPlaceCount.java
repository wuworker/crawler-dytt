package com.wxl.dyttcrawler.web.dto.statistic;

import com.wxl.dyttcrawler.web.dto.AggTwoResult;

/**
 * Create by wuxingle on 2020/7/13
 * 年份地区数据
 */
public class YearPlaceCount extends AggTwoResult<String, String, Integer> {

    private static final long serialVersionUID = 7480660194107013629L;

    /**
     * 新增地区数据
     */
    public void add(String key, PlaceCount placeCount) {
        super.add(key, placeCount);
    }

}
