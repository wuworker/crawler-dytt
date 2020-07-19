package com.wxl.dyttcrawler.web.service;

import com.wxl.dyttcrawler.core.Crawler;
import com.wxl.dyttcrawler.web.dto.crawler.CrawlerProgress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

/**
 * Create by wuxingle on 2020/7/19
 * 爬虫管理service
 */
@Slf4j
@Service
public class DyttCrawlerService {

    @Autowired
    private Crawler crawler;

    @Autowired
    private Scheduler scheduler;

    /**
     * 手动同步调用url
     */
    public void manualInvokeUrl(String url) {
        crawler.crawl(url);
    }

    /**
     * 异步启动爬虫
     */
    public boolean startCrawler() {
        return crawler.start();
    }

    /**
     * 停止爬虫
     */
    public boolean stopCrawler() {
        return crawler.stop();
    }

    /**
     * 获取爬虫状态
     */
    public Crawler.Status statusCrawler() {
        return crawler.getStatus();
    }

    /**
     * 获取爬虫进度
     */
    public CrawlerProgress getCrawlerProgress() {
        if (scheduler instanceof MonitorableScheduler) {
            MonitorableScheduler monitorableScheduler = (MonitorableScheduler) scheduler;

            int todoSize = monitorableScheduler.getLeftRequestsCount(crawler);
            int totalSize = monitorableScheduler.getTotalRequestsCount(crawler);
            return new CrawlerProgress(todoSize, totalSize);
        }
        return new CrawlerProgress();
    }

    /**
     * 重置消费
     */
    public boolean resetCrawlerProgress() {
        if (scheduler instanceof DuplicateRemover) {
            ((DuplicateRemover) scheduler).resetDuplicateCheck(crawler);
            return true;
        }
        return false;
    }

}





