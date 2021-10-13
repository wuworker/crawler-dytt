package com.wxl.dyttcrawler.web.service

import com.wxl.dyttcrawler.core.Crawler
import com.wxl.dyttcrawler.scheduler.ProcessFailScheduler
import com.wxl.dyttcrawler.web.dto.crawler.CrawlerProgress
import org.springframework.stereotype.Service
import us.codecraft.webmagic.scheduler.MonitorableScheduler
import us.codecraft.webmagic.scheduler.Scheduler
import us.codecraft.webmagic.scheduler.component.DuplicateRemover

/**
 * Create by wuxingle on 2021/10/13
 * 爬虫管理service
 */
@Service
class DyttCrawlerService(
    val crawler: Crawler,
    val scheduler: Scheduler
) {

    /**
     * 手动调用url
     */
    fun crawlUrl(url: String) = crawler.crawl(url)

    /**
     * 异步启动爬虫
     */
    fun startCrawler() = crawler.start()

    /**
     * 停止爬虫
     */
    fun stopCrawler() = crawler.stop()

    /**
     * 获取爬虫状态
     */
    fun statusCrawler() = crawler.getStatus()

    /**
     * 获取爬虫进度
     */
    fun getCrawlerProgress(): CrawlerProgress {
        val crawlerProgress = CrawlerProgress()
        val scheduler = this.scheduler
        if (scheduler is MonitorableScheduler) {
            crawlerProgress.todoSize = scheduler.getLeftRequestsCount(crawler)
            crawlerProgress.totalSize = scheduler.getTotalRequestsCount(crawler)
        }
        if (scheduler is ProcessFailScheduler) {
            crawlerProgress.failSize = scheduler.getFailCount(crawler)
        }
        return crawlerProgress
    }

    /**
     * 重置消费
     */
    fun resetCrawlerProgress(): Boolean {
        val scheduler = this.scheduler
        if (scheduler is DuplicateRemover) {
            scheduler.resetDuplicateCheck(crawler)
            return true
        }
        return false
    }

}