package com.wxl.dyttcrawler.web.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Create by wuxingle on 2020/7/13
 * 各维度数量
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseStatCount implements Serializable {

    private static final long serialVersionUID = 1035844495591241604L;

    /**
     * 总数
     */
    private Long count;

    /**
     * 种类
     */
    private Long category;

    /**
     * 产地
     */
    private Long place;

    /**
     * 语言
     */
    private Long language;
}
