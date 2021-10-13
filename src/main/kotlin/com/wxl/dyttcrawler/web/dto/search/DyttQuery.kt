package com.wxl.dyttcrawler.web.dto.search

/**
 * Create by wuxingle on 2021/10/13
 * 电影查询
 */
data class DyttQuery(
    // 标题,片名,译名
    var title: String? = null,

    // 年代起始
    var yearStart: Int? = null,

    // 年代结束
    var yearEnd: Int? = null,

    // 产地
    var originPlace: List<String>? = null,

    // 类别
    var category: List<String>? = null,

    // 主演,导演,编剧
    var people: String? = null,

    // 标签
    var tags: List<String>? = null,

    // 起始位置
    var from: Int? = 0,

    // 查询大小
    var size: Int? = 10,
) {

    fun initFromSize() {
        if (from == null || from!! < 0) {
            from = 0
        }
        if (size == null || size!! <= 0) {
            size = 10
        }
    }

}