package com.wxl.crawlerdytt.core;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Create by wuxingle on 2020/5/1
 * 电影url
 */
@Data
@Builder
public class DyttUrl implements Serializable {

    private static final long serialVersionUID = 6647570962420163236L;

    public static final String TYPE_OF_LINKS = "links";

    public static final String TYPE_OF_DETAIL = "detail";

    private String url;

    private String path;

    // 链接类型
    private String type;

    // 层次
    private int layer;

    // 权重
    private int weight;
}
