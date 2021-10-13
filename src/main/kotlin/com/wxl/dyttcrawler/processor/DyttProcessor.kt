package com.wxl.dyttcrawler.processor

import us.codecraft.webmagic.Page

/**
 * Create by wuxingle on 2021/10/12
 * 页面匹配处理
 */
interface DyttProcessor {

    fun process(page: Page)

    fun match(page: Page): Boolean
}