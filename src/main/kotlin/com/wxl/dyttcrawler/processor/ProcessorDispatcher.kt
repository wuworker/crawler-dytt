package com.wxl.dyttcrawler.processor

import org.slf4j.LoggerFactory
import us.codecraft.webmagic.Page
import us.codecraft.webmagic.Site
import us.codecraft.webmagic.processor.PageProcessor

/**
 * Create by wuxingle on 2021/10/12
 * 页面处理分发
 */
class ProcessorDispatcher(
    private val site: Site,
    private val processors: List<DyttProcessor>
) : PageProcessor {

    companion object {
        private val log = LoggerFactory.getLogger(ProcessorDispatcher::class.java)
    }

    override fun process(page: Page) {
        val processor = getProcessor(page)
        if (processor == null) {
            log.debug("ignore page:{}", page.url)
        }
        processor?.process(page)
    }

    override fun getSite(): Site = site

    private fun getProcessor(page: Page): DyttProcessor? {
        for (processor in processors) {
            if (processor.match(page)) {
                return processor
            }
        }
        return null
    }

}
