package com.wxl.crawlerdytt.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wxl.crawlerdytt.core.DyttConstants;
import com.wxl.crawlerdytt.processor.DyttProcessor;
import com.wxl.crawlerdytt.processor.ProcessorDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Create by wuxingle on 2020/5/10
 * 电源天堂爬虫配置
 */
@Slf4j
@Configuration
public class DyttCrawlerConfiguration {

    @Bean
    public Spider spider(Downloader downloader,
                         ObjectProvider<Pipeline> pipelines,
                         ExecutorService executorService,
                         PageProcessor pageProcessor,
                         Scheduler scheduler) {
        Spider spider = Spider.create(pageProcessor)
                .addUrl("https://www.dytt8.net/html/gndy/dyzz/20200506/59996.html")
                .setExecutorService(executorService)
                .thread(1)
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
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("dytt-pool-%s")
                .build();

        return new ThreadPoolExecutor(1, 10,
                60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10),
                threadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 队列管理
     */
    @Bean
    public Scheduler crawlerScheduler() {
        QueueScheduler queueScheduler = new QueueScheduler();
        queueScheduler.setDuplicateRemover(new HashSetDuplicateRemover());

        return queueScheduler;
    }

    /**
     * 页面处理
     */
    @Bean
    public ProcessorDispatcher processorDispatcher(ObjectProvider<DyttProcessor> processors) {
        Site site = Site.me().setCharset(DyttConstants.DEFAULT_CHARSET).setRetryTimes(1).setTimeOut(10000).setSleepTime(1000);
        List<DyttProcessor> collect = processors.orderedStream().collect(Collectors.toList());
        return new ProcessorDispatcher(site, collect);
    }

}
