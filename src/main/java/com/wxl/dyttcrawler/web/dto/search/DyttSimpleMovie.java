package com.wxl.dyttcrawler.web.dto.search;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Create by wuxingle on 2020/7/17
 * 电影简单对象
 */
@Data
public class DyttSimpleMovie implements Serializable {

    private static final long serialVersionUID = -2563172955833336876L;

    private String id;

    // 标题
    private String title;

    // 地址链接
    private String url;

    // 封面图片地址
    private String picUrl;

    // 片名
    private String name;

    // 年代
    private Integer year;

    // 产地
    private List<String> originPlace;

    // 类别
    private List<String> category;

    // 豆瓣评分
    private Double score;

    // 标签
    private List<String> tags;

}
