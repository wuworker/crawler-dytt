package com.wxl.dyttcrawler.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Create by wuxingle on 2020/5/2
 * 电影详情
 */
@Data
public class DyttMovie implements Serializable {

    private static final long serialVersionUID = 4872108258435579856L;

    private String id;

    // 标题
    private String title;

    // 地址链接
    private String url;

    // 封面图片地址
    private String picUrl;

    // 译名
    private List<String> translateNames;

    // 片名
    private String name;

    // 年代
    private Integer year;

    // 产地
    private List<String> originPlace;

    // 类别
    private List<String> category;

    // 语言
    private List<String> language;

    // 字幕
    private String words;

    // 豆瓣评分
    private Double score;

    // 评分人数
    private Integer scoreNums;

    // 上映时间
    private List<DyttReleaseDate> releaseDates;

    // 导演
    private List<String> director;

    // 编剧
    private List<String> screenwriter;

    // 主演
    private List<String> act;

    // 标签
    private List<String> tags;

    // 获奖情况
    private List<String> awards;

    // 简介
    private String desc;

    // 发布时间
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date publishDate;

    // 下载地址
    private List<String> downLinks;

    // 文件大小 gb
    private Double fileSize;

    // 该文档更新时间
    @JsonFormat
    private Date updateTime;
}
