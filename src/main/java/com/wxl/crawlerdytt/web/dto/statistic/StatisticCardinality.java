package com.wxl.crawlerdytt.web.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Create by wuxingle on 2020/7/13
 * 基数统计
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticCardinality implements Serializable {

    private static final long serialVersionUID = 1035844495591241604L;

    /**
     * 种类
     */
    private Integer category;

    /**
     * 产地
     */
    private Integer place;

    /**
     * 语言
     */
    private Integer lanuage;
}
