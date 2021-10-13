package com.wxl.dyttcrawler.urlhandler

import us.codecraft.webmagic.Page
import java.net.URL

/**
 * Create by wuxingle on 2021/10/12
 * 链接优先级计算
 */
fun interface PriorityUrlCalculator {

    /**
     * 计算url优先级
     */
    fun calculate(page: Page, url: URL): Int
}

