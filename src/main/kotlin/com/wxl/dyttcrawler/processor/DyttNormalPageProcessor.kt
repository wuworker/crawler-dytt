package com.wxl.dyttcrawler.processor

import com.wxl.dyttcrawler.urlhandler.PriorityUrlCalculator
import com.wxl.dyttcrawler.urlhandler.UrlFilter
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import us.codecraft.webmagic.Page

/**
 * Create by wuxingle on 2021/10/12
 * 普通页处理,提取link
 */
@Order(Ordered.LOWEST_PRECEDENCE)
@Component
class DyttNormalPageProcessor(
    priorityUrlCalculator: PriorityUrlCalculator,
    urlFilter: UrlFilter
) : AbstractDyttProcessor(priorityUrlCalculator, urlFilter) {

    override fun process(page: Page) {
        addLinks(page)
    }

    override fun match(page: Page): Boolean = true
}

