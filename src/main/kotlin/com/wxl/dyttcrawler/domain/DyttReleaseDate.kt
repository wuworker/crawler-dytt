package com.wxl.dyttcrawler.domain

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

/**
 * Create by wuxingle on 2021/10/12
 * 上映时间
 */
data class DyttReleaseDate(

    // 时间
    @JsonFormat(pattern = "yyyy-MM-dd")
    var date: Date? = null,

    // 地区
    var place: String? = null
)
