package com.wxl.crawlerdytt.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wxl.crawlerdytt.processor.DyttProcessor;
import com.wxl.crawlerdytt.processor.ProcessorDispatcher;
import com.wxl.crawlerdytt.properties.CrawlerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PriorityScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Create by wuxingle on 2020/5/10
 * 爬虫配置
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(CrawlerProperties.class)
public class CrawlerConfiguration {

    private CrawlerProperties crawlerProperties;

    @Autowired
    public CrawlerConfiguration(CrawlerProperties crawlerProperties) {
        this.crawlerProperties = crawlerProperties;
    }

    @Bean
    public Spider spider(Downloader downloader,
                         ObjectProvider<Pipeline> pipelines,
                         ExecutorService executorService,
                         PageProcessor pageProcessor,
                         Scheduler scheduler) {
        String firstUrl = crawlerProperties.getFirstUrl();
        Integer threads = crawlerProperties.getPool().getMaxSize();

        Spider spider = Spider.create(pageProcessor)
                .addUrl(firstUrl)
                .setExecutorService(executorService)
                .thread(threads)
                .setExitWhenComplete(true)
                .setScheduler(scheduler)
                .setDownloader(downloader);
        pipelines.orderedStream().forEach(spider::addPipeline);
        return spider;
    }

    /**
     * 线程池
     */
    @Bean
    public ExecutorService crawlerExecutorService() {
        CrawlerProperties.PoolProperties pool = crawlerProperties.getPool();

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat(pool.getThreadNameFormat())
                .build();

        return new ThreadPoolExecutor(pool.getCoreSize(), pool.getMaxSize(),
                pool.getKeepAlive().getSeconds(), TimeUnit.SECONDS, new ArrayBlockingQueue<>(pool.getQueueSize()),
                threadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 队列管理
     */
    @Bean
    public Scheduler crawlerScheduler() {
        PriorityScheduler priorityScheduler = new PriorityScheduler();
        priorityScheduler.setDuplicateRemover(new HashSetDuplicateRemover());

        return priorityScheduler;
    }

    /**
     * 页面处理
     */
    @Bean
    public ProcessorDispatcher processorDispatcher(ObjectProvider<DyttProcessor> processors) {
        CrawlerProperties.SiteProperties siteProp = crawlerProperties.getSite();

        Site site = Site.me()
                .setCharset(crawlerProperties.getCharset())
                .setUserAgent(siteProp.getUserAgent())
                .setSleepTime((int) siteProp.getSleepTime().toMillis())
                .setRetryTimes(siteProp.getRetryTimes())
                .setRetrySleepTime((int) siteProp.getRetrySleepTime().toMillis())
                .setTimeOut((int) siteProp.getTimeout().toMillis())
                .setDisableCookieManagement(siteProp.isDisableCookie());

        if (!CollectionUtils.isEmpty(siteProp.getAcceptStatusCode())) {
            site.setAcceptStatCode(new HashSet<>(siteProp.getAcceptStatusCode()));
        }
        if (!CollectionUtils.isEmpty(siteProp.getHeaders())) {
            siteProp.getHeaders().forEach(site::addHeader);
        }

        List<DyttProcessor> collect = processors.orderedStream().collect(Collectors.toList());
        return new ProcessorDispatcher(site, collect);
    }

}
