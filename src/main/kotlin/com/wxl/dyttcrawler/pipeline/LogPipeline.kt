package com.wxl.dyttcrawler.pipeline

import com.wxl.dyttcrawler.domain.DyttMovie
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import us.codecraft.webmagic.ResultItems
import us.codecraft.webmagic.Task

/**
 * Create by wuxingle on 2021/10/11
 * log
 */
@Component
class LogPipeline : DyttPipeline<DyttMovie>(DyttMovie::class.java) {

    companion object {
        private val log = LoggerFactory.getLogger(LogPipeline::class.java)
    }

    override fun process(obj: DyttMovie, resultItems: ResultItems, task: Task) {
        log.info("process result:{}, {}", obj.url, obj.title)
    }

}
