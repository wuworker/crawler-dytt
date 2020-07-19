package com.wxl.dyttcrawler.web.dto.statistic;

import com.wxl.dyttcrawler.web.dto.AggOneResult;

import java.util.Map;
import java.util.TreeMap;

/**
 * Create by wuxingle on 2020/7/13
 * 月份数统计
 * key为月份
 * value为数量
 */
public class MonthCount extends AggOneResult<Integer, Integer> {

    private static final long serialVersionUID = 5812310689332154120L;

    /**
     * 补齐月份
     */
    public void fillMonth() {
        for (int i = 1; i <= 12; i++) {
            data.putIfAbsent(i, 0);
        }
    }


    /**
     * 月份按顺序显示
     */
    @Override
    protected Map<Integer, Integer> createDataMap() {
        return new TreeMap<>();
    }
}
