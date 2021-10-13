package com.wxl.dyttcrawler.core

import java.util.regex.Pattern

/**
 * Create by wuxingle on 2021/10/09
 * 正则常量
 */
object DyttPattern {

    /**
     * 爬取协议正则
     */
    @JvmField
    val PROTOCOL_PATTERN: Pattern = Pattern.compile("http|https")

    /**
     * 爬取域名正则
     */
    @JvmField
    val DOMAIN_PATTERN: Pattern = Pattern.compile("(www\\.dytt8\\.net)|(www\\.ygdy8\\.com)")

    /**
     * 网站首页
     */
    @JvmField
    val INDEX_PATTERN: Pattern = Pattern.compile("/?|/index\\.html?")

    /**
     * 国内电影首页
     */
    @JvmField
    val GNDY_INDEX_PATTERN: Pattern = Pattern.compile("/html/gndy(/?|(/index\\.html)?)")

    /**
     * 国内电影详情页路径
     */
    @JvmField
    val GNDY_DETAIL_PATH_PATTERN: Pattern = Pattern.compile("/html/gndy/\\w+/(\\d{4})(\\d{2})(\\d{2})/(\\d+)\\.html")

    /**
     * 电影分类列表页路径
     */
    @JvmField
    val CATEGORY_LIST_PATH_PATTERN: Pattern = Pattern.compile("/html/gndy/\\w+(/?|(/index\\.html)?)")
}

/**
 * 请求属性
 */
object RequestAttr {

    /**
     * 链接深度
     */
    const val DEPTH = "_depth"
}

/**
 * es常量
 */
object ElasticConstants {

    const val DYTT_INDEX: String = "dytt"

    const val DYTT_TYPE: String = "_doc"

}
