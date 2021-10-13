package com.wxl.dyttcrawler.domain

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

/**
 * Create by wuxingle on 2021/10/12
 * 电影详情
 */
data class DyttMovie(
    var id: String? = null,

    // 标题
    var title: String? = null,

    // 地址链接
    var url: String? = null,

    // 封面图片地址
    var picUrl: String? = null,

    // 译名
    var translateNames: List<String>? = null,

    // 片名
    var name: String? = null,

    // 年代
    var year: Int? = null,

    // 产地
    var originPlace: List<String>? = null,

    // 类别
    var category: List<String>? = null,

    // 语言
    var language: List<String>? = null,

    // 字幕
    var words: String? = null,

    // 豆瓣评分
    var score: Double? = null,

    // 评分人数
    var scoreNums: Int? = null,

    // 上映时间
    var releaseDates: List<DyttReleaseDate>? = null,

    // 导演
    var director: List<String>? = null,

    // 编剧
    var screenwriter: List<String>? = null,

    // 主演
    var act: List<String>? = null,

    // 标签
    var tags: List<String>? = null,

    // 获奖情况
    var awards: List<String>? = null,

    // 简介
    var desc: String? = null,

    // 发布时间
    @JsonFormat(pattern = "yyyy-MM-dd")
    var publishDate: Date? = null,

    // 下载地址
    var downLinks: List<String>? = null,

    // 文件大小 gb
    var fileSize: Double? = null,

    // 该文档更新时间
    @JsonFormat
    var updateTime: Date? = null

)