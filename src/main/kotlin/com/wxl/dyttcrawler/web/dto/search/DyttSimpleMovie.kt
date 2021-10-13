package com.wxl.dyttcrawler.web.dto.search

/**
 * Create by wuxingle on 2021/10/13
 * 电影简单对象
 */
data class DyttSimpleMovie(
    var id: String? = null,

    // 标题
    var title: String? = null,

    // 地址链接
    var url: String? = null,

    // 封面图片地址
    var picUrl: String? = null,

    // 片名
    var name: String? = null,

    // 年代
    var year: Int? = null,

    // 产地
    var originPlace: List<String>? = null,

    // 类别
    var category: List<String>? = null,

    // 豆瓣评分
    var score: Double? = null,

    // 标签
    var tags: List<String>? = null
)
