package com.wxl.dyttcrawler.config

import com.wxl.dyttcrawler.core.Crawler
import com.wxl.dyttcrawler.core.CrawlerListener
import com.wxl.dyttcrawler.core.ExecutorThreadPool
import com.wxl.dyttcrawler.core.ThreadPool
import com.wxl.dyttcrawler.downloader.HttpDownloader
import com.wxl.dyttcrawler.processor.DyttProcessor
import com.wxl.dyttcrawler.processor.ProcessorDispatcher
import com.wxl.dyttcrawler.properties.CrawlerProperties
import com.wxl.dyttcrawler.properties.SiteProperties
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import us.codecraft.webmagic.Request
import us.codecraft.webmagic.Site
import us.codecraft.webmagic.pipeline.Pipeline
import us.codecraft.webmagic.processor.PageProcessor
import us.codecraft.webmagic.scheduler.Scheduler
import java.util.stream.Collectors

/**
 * Create by wuxingle on 2021/10/02
 * 爬虫配置
 */
@Configuration
class CrawlerConfiguration(
    private val crawlerProperties: CrawlerProperties,
    private val siteProperties: SiteProperties,
    private val environment: Environment
) {

    /**
     * 爬虫管理
     */
    @Bean(destroyMethod = "close")
    fun crawler(
        pageProcessor: PageProcessor,
        downloader: HttpDownloader,
        pipelines: ObjectProvider<Pipeline>,
        threadPool: ThreadPool,
        scheduler: Scheduler,
        listeners: ObjectProvider<CrawlerListener>
    ): Crawler? {
        val taskId = environment.getRequiredProperty("spring.application.name")
        val startUrl = crawlerProperties.startUrl

        return Crawler.build {
            this.taskId = taskId
            this.pageProcessor = pageProcessor
            this.downloader = downloader
            this.pipelines = pipelines.orderedStream().collect(Collectors.toList())
            this.scheduler = scheduler
            this.threadPool = threadPool
            this.startRequests = listOf(Request(startUrl))
            this.exitWhenComplete = true
            this.crawlerListeners = listeners.orderedStream().collect(Collectors.toList())
        }
    }

    /**
     * 线程池
     */
    @Bean
    fun crawlerExecutorService(): ThreadPool {
        return ExecutorThreadPool(crawlerProperties.maxThreads)
    }

    /**
     * 页面处理
     */
    @Bean
    fun processorDispatcher(site: Site, processors: ObjectProvider<DyttProcessor>): ProcessorDispatcher {
        val collect: List<DyttProcessor> = processors.orderedStream().collect(Collectors.toList())
        return ProcessorDispatcher(site, collect)
    }

    /**
     * site配置
     */
    @Bean
    fun dyttPrimarySite(): Site = Site.me().apply {
        domain = siteProperties.domain
        charset = siteProperties.charset
        userAgent = siteProperties.userAgent
        sleepTime = siteProperties.sleepTime.toMillis().toInt()
        retryTimes = siteProperties.retryTimes
        cycleRetryTimes = siteProperties.cycleRetryTimes
        retrySleepTime = siteProperties.retrySleepTime.toMillis().toInt()
        timeOut = siteProperties.timeout.toMillis().toInt()
        if (siteProperties.acceptStatusCode.isNotEmpty()) {
            acceptStatCode = siteProperties.acceptStatusCode.toSet()
        }
        if (siteProperties.headers.isNotEmpty()) {
            for ((k, v) in siteProperties.headers) {
                addHeader(k, v)
            }
        }
        isUseGzip = siteProperties.useGzip
        isDisableCookieManagement = true
    }

}