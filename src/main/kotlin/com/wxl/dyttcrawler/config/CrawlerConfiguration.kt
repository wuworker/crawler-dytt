package com.wxl.dyttcrawler.config

import com.wxl.dyttcrawler.core.Crawler
import com.wxl.dyttcrawler.core.CrawlerListener
import com.wxl.dyttcrawler.core.ExecutorThreadPool
import com.wxl.dyttcrawler.core.ThreadPool
import com.wxl.dyttcrawler.processor.DyttProcessor
import com.wxl.dyttcrawler.processor.ProcessorDispatcher
import com.wxl.dyttcrawler.properties.CrawlerProperties
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.env.Environment
import us.codecraft.webmagic.Request
import us.codecraft.webmagic.Site
import us.codecraft.webmagic.downloader.Downloader
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
    private val environment: Environment
) {

    /**
     * 爬虫管理
     */
    @Bean(destroyMethod = "close")
    fun crawler(
        pageProcessor: PageProcessor,
        downloader: Downloader,
        pipelines: ObjectProvider<Pipeline>,
        threadPool: ThreadPool,
        scheduler: Scheduler,
        listeners: ObjectProvider<CrawlerListener>
    ): Crawler? {
        val taskId = environment.getRequiredProperty("spring.application.name")
        val firstUrl = crawlerProperties.firstUrl

        return Crawler.build {
            this.taskId = taskId
            this.pageProcessor = pageProcessor
            this.downloader = downloader
            this.pipelines = pipelines.orderedStream().collect(Collectors.toList())
            this.scheduler = scheduler
            this.threadPool = threadPool
            this.startRequests = listOf(Request(firstUrl))
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
    @Primary
    @Bean("dyttPrimarySite")
    fun dyttPrimarySite(): Site {
        val siteProp = crawlerProperties.site

        return Site.me().apply {
            domain = "dytt"
            charset = crawlerProperties.charset
            userAgent = siteProp.userAgent
            sleepTime = siteProp.sleepTime.toMillis().toInt()
            retryTimes = siteProp.retryTimes
            retrySleepTime = siteProp.retrySleepTime.toMillis().toInt()
            timeOut = siteProp.timeout.toMillis().toInt()
            isDisableCookieManagement = siteProp.disableCookie
            if (siteProp.acceptStatusCode.isNotEmpty()) {
                acceptStatCode = siteProp.acceptStatusCode.toSet()
            }
            if (siteProp.headers.isNotEmpty()) {
                for ((k, v) in siteProp.headers) {
                    addHeader(k, v)
                }
            }
        }
    }

}