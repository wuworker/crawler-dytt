package com.wxl.dyttcrawler.config;

import com.wxl.dyttcrawler.core.*;
import com.wxl.dyttcrawler.processor.DyttProcessor;
import com.wxl.dyttcrawler.processor.ProcessorDispatcher;
import com.wxl.dyttcrawler.properties.CrawlerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;

import java.util.HashSet;
import java.util.List;
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

    private Environment environment;

    @Autowired
    public CrawlerConfiguration(CrawlerProperties crawlerProperties,
                                Environment environment) {
        this.crawlerProperties = crawlerProperties;
        this.environment = environment;
    }

    /**
     * 爬虫管理
     */
    @Bean(destroyMethod = "close")
    public Crawler crawler(PageProcessor pageProcessor,
                           Downloader downloader,
                           ObjectProvider<Pipeline> pipelines,
                           ThreadPool threadPool,
                           Scheduler scheduler,
                           ObjectProvider<CrawlerListener> listeners) {
        String taskId = environment.getRequiredProperty("spring.application.name");
        String firstUrl = crawlerProperties.getFirstUrl();

        return Crawlers.create()
                .setTaskId(taskId)
                .setPageProcessor(pageProcessor)
                .setDownloader(downloader)
                .addPipeline(pipelines.orderedStream().collect(Collectors.toList()))
                .setScheduler(scheduler)
                .setThreadPool(threadPool)
                .startUrls(firstUrl)
                .addCrawlerListeners(listeners.orderedStream().collect(Collectors.toList()))
                .setExitWhenComplete(true)
                .build();
    }

    /**
     * 线程池
     */
    @Bean
    public ThreadPool crawlerExecutorService() {
        return new ExecutorThreadPool(crawlerProperties.getMaxThreads());
    }

    /**
     * 页面处理
     */
    @Bean
    public ProcessorDispatcher processorDispatcher(Site site, ObjectProvider<DyttProcessor> processors) {
        List<DyttProcessor> collect = processors.orderedStream().collect(Collectors.toList());
        return new ProcessorDispatcher(site, collect);
    }

    /**
     * site配置
     */
    @Primary
    @Bean("dyttPrimarySite")
    public Site dyttPrimarySite() {
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
        return site;
    }

}
