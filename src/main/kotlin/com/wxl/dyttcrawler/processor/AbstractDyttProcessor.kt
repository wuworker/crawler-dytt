package com.wxl.dyttcrawler.processor

import com.wxl.dyttcrawler.core.RequestAttr
import com.wxl.dyttcrawler.urlhandler.PriorityUrlCalculator
import com.wxl.dyttcrawler.urlhandler.UrlFilter
import us.codecraft.webmagic.Page
import us.codecraft.webmagic.Request
import us.codecraft.webmagic.utils.UrlUtils
import java.net.MalformedURLException
import java.net.URL

/**
 * Create by wuxingle on 2021/10/12
 * 包含Link相关处理
 */
abstract class AbstractDyttProcessor(
    /**
     * url优先级计算
     */
    private val priorityUrlCalculator: PriorityUrlCalculator,

    /**
     * url过滤器
     */
    private val urlFilter: UrlFilter
) : DyttProcessor {


    /**
     * 添加link
     */
    fun addLinks(page: Page) {
        val all = page.html.links().all()
        all.mapNotNull { canonicalizeUrl(page, it) }
            .filter { urlFilter.match(it) }
            .forEach { addExtraLink(page, it) }
    }


    fun addLink(page: Page, url: String) {
        val newUrl = canonicalizeUrl(page, url)
        if (newUrl != null && urlFilter.match(newUrl)) {
            addExtraLink(page, newUrl)
        }
    }

    /**
     * 存结果对象
     */
    fun putObject(page: Page, obj: Any?) {
        if (obj != null) {
            page.putField(obj::class.java.name, obj)
        }
    }


    /**
     * url格式化
     *
     * @return 绝对地址
     */
    private fun canonicalizeUrl(page: Page, url: String?): URL? {
        if (url.isNullOrBlank() || "#" == url) {
            return null
        }
        val newUrl = UrlUtils.canonicalizeUrl(url, page.url.toString())
        if (newUrl.isNotBlank()) {
            try {
                return URL(newUrl)
            } catch (e: MalformedURLException) {
                //ignore
            }
        }
        return null
    }


    private fun addExtraLink(page: Page, url: URL) {
        val request = Request()
        request.url = url.toString()

        val priority = priorityUrlCalculator.calculate(page, url)
        request.priority = priority.toLong()

        val depth = page.request.getExtra(RequestAttr.DEPTH) as Int?
        request.extras = mutableMapOf<String, Any>(RequestAttr.DEPTH to (depth ?: 0))

        page.addTargetRequest(request)
    }


}

