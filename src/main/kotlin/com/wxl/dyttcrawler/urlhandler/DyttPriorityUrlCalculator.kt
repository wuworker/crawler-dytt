package com.wxl.dyttcrawler.urlhandler

import com.wxl.dyttcrawler.core.DyttPattern.CATEGORY_LIST_PATH_PATTERN
import com.wxl.dyttcrawler.core.DyttPattern.GNDY_DETAIL_PATH_PATTERN
import com.wxl.dyttcrawler.core.DyttPattern.GNDY_INDEX_PATTERN
import com.wxl.dyttcrawler.core.DyttPattern.INDEX_PATTERN
import com.wxl.dyttcrawler.core.RequestAttr
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import us.codecraft.webmagic.Page
import java.net.URL

/**
 * Create by wuxingle on 2021/10/12
 * url优先级计算
 * 详情页优先
 */
@Component
class DyttPriorityUrlCalculator : PriorityUrlCalculator {

    /**
     * 链接相关度权重
     */
    var correlationWeight = 0.7f

    /**
     * 深度权重
     */
    var depthWeight = 0.3f

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun calculate(page: Page, url: URL): Int {
        val correlation = correlationWeight * correlation(url)
        val depth = depthWeight * depth(page)

        val sum = (correlation + depth).toInt()
        log.trace("url priority result:{}, correlation:{}, depth:{}", sum, correlation, depth)
        return sum
    }

    /**
     * 链接相关度计算
     */
    private fun correlation(url: URL): Int {
        val path = url.path
        return when {
            // 首页
            INDEX_PATTERN.matcher(path).matches() -> 100
            // 详情页
            GNDY_DETAIL_PATH_PATTERN.matcher(path).matches() -> 100
            // 列表页
            GNDY_INDEX_PATTERN.matcher(path).matches() -> 50
            CATEGORY_LIST_PATH_PATTERN.matcher(path).matches() -> 50

            else -> 30
        }
    }


    /**
     * 深度计算
     */
    private fun depth(page: Page): Int {
        val depth = page.request.getExtra(RequestAttr.DEPTH) as Int?
        return when {
            depth == null -> 100
            depth < 10 -> (10 - depth) * 10
            else -> 0
        }
    }

}