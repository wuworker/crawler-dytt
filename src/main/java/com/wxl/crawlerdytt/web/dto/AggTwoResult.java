package com.wxl.crawlerdytt.web.dto;

/**
 * Create by wuxingle on 2020/7/13
 * 二次聚合结果
 */
public class AggTwoResult<K, K1, V1> extends AggOneResult<K, AggOneResult<K1, V1>> {

    private static final long serialVersionUID = -6081746091174816661L;

}
