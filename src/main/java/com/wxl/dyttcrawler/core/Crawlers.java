package com.wxl.dyttcrawler.core;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Create by wuxingle on 2020/6/7
 * Crawler builder
 */
public class Crawlers {

    /**
     * 任务uuid
     */
    private String taskId;

    /**
     * 页面处理
     */
    private PageProcessor pageProcessor;

    /**
     * 页面下载
     */
    private Downloader downloader;

    /**
     * 结果处理
     */
    private List<Pipeline> pipelines = new ArrayList<>();

    /**
     * 任务队列
     */
    private Scheduler scheduler;

    /**
     * 执行线程池
     */
    private ThreadPool threadPool;

    /**
     * 开始的请求
     */
    private List<Request> startRequests = new ArrayList<>();

    /**
     * 监听器
     */
    private List<CrawlerListener> crawlerListeners = new ArrayList<>();

    /**
     * 完成时是否结束
     */
    private boolean exitWhenComplete;


    private Crawlers() {
    }

    public static Crawlers create() {
        return new Crawlers();
    }

    public Crawlers setTaskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public Crawlers setPageProcessor(PageProcessor pageProcessor) {
        this.pageProcessor = pageProcessor;
        return this;
    }

    public Crawlers setDownloader(Downloader downloader) {
        this.downloader = downloader;
        return this;
    }

    public Crawlers addPipeline(Pipeline... pipeline) {
        pipelines.addAll(Arrays.asList(pipeline));
        return this;
    }

    public Crawlers addPipeline(Collection<Pipeline> pipeline) {
        pipelines.addAll(pipeline);
        return this;
    }

    public Crawlers setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public Crawlers setThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
        return this;
    }

    public Crawlers startUrls(String... startUrls) {
        for (String startUrl : startUrls) {
            startRequests.add(new Request(startUrl));
        }
        return this;
    }

    public Crawlers startUrls(Request... startUrls) {
        startRequests.addAll(Arrays.asList(startUrls));
        return this;
    }

    public Crawlers addCrawlerListeners(CrawlerListener... listeners) {
        crawlerListeners.addAll(Arrays.asList(listeners));
        return this;
    }

    public Crawlers addCrawlerListeners(Collection<CrawlerListener> listeners) {
        crawlerListeners.addAll(listeners);
        return this;
    }

    public Crawlers setExitWhenComplete(boolean exitWhenComplete) {
        this.exitWhenComplete = exitWhenComplete;
        return this;
    }

    public Crawler build() {
        Crawler crawler = new Crawler(taskId, pageProcessor,
                downloader, pipelines, scheduler, threadPool,
                startRequests, exitWhenComplete);
        crawler.addCrawlerListener(crawlerListeners.toArray(new CrawlerListener[0]));
        return crawler;
    }

}

