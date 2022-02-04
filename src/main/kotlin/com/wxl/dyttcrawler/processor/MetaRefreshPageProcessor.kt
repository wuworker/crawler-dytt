package com.wxl.dyttcrawler.processor

import com.wxl.dyttcrawler.urlhandler.PriorityUrlCalculator
import com.wxl.dyttcrawler.urlhandler.UrlFilter
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import us.codecraft.webmagic.Page
import java.util.regex.Pattern

/**
 * Create by wuxingle on 2021/10/12
 * 处理浏览器自动刷新的标签
 * <head>
 * <meta http-equiv="refresh" content="0;URL=index.htm">
 * </head>
 */
@Order(-100)
@Component
class MetaRefreshPageProcessor(
    priorityUrlCalculator: PriorityUrlCalculator,
    urlFilter: UrlFilter
) : AbstractDyttProcessor(priorityUrlCalculator, urlFilter) {

    companion object {
        private val log = LoggerFactory.getLogger(MetaRefreshPageProcessor::class.java)

        private val LOCATION_PATTERN = Pattern.compile("URL=(.+)")
    }

    override fun process(page: Page) {
        log.debug("process refresh meta:{}", page.url)

        val refreshMeta = getRefreshMeta(page)
        val content = refreshMeta?.attr("content")
        if (!content.isNullOrBlank()) {
            val matcher = LOCATION_PATTERN.matcher(content)
            if (matcher.find()) {
                val location = matcher.group(1)
                addLink(page, location)
            }
        }
    }

    override fun match(page: Page): Boolean {
        return getRefreshMeta(page) !== null
    }

    private fun getRefreshMeta(page: Page): Element? {
        val head = page.html.document.head()
        return head.select("meta[http-equiv=refresh]").first()
    }

}