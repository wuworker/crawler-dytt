package com.wxl.dyttcrawler.web.dto.statistic;

import com.wxl.dyttcrawler.web.dto.Item;
import lombok.NoArgsConstructor;

/**
 * Create by wuxingle on 2020/7/13
 * 地区统计数据
 */
@NoArgsConstructor
public class PlaceCount extends Item<String, Long> {

    private static final long serialVersionUID = 9139820065990605593L;

    public PlaceCount(String key, Long value) {
        super(key, value);
    }
}
