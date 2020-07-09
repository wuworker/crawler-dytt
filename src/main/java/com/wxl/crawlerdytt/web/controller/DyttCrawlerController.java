package com.wxl.crawlerdytt.web.controller;

import com.wxl.crawlerdytt.core.Crawler;
import com.wxl.crawlerdytt.properties.CrawlerProperties;
import com.wxl.crawlerdytt.web.dto.ConsumerProgressBO;
import com.wxl.crawlerdytt.web.dto.ResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import static com.wxl.crawlerdytt.web.dto.ResultCode.SCHEDULE_CANNOT_RESET;
import static com.wxl.crawlerdytt.web.dto.ResultCode.SCHEDULE_IS_NOT_MONITOR;

/**
 * Create by wuxingle on 2020/5/27
 * 爬虫相关接口
 */
@Slf4j
@RestController
@RequestMapping("/dytt/crawler")
@EnableConfigurationProperties(CrawlerProperties.class)
public class DyttCrawlerController {

    @Autowired
    private Crawler crawler;

    @Autowired
    private Scheduler scheduler;

    /**
     * 启动
     */
    @GetMapping("/start")
    public ResultDTO<Boolean> startCrawler() {
        boolean start = crawler.start();
        return ResultDTO.ok(start);
    }


    /**
     * 停止
     */
    @GetMapping("/stop")
    public ResultDTO<Boolean> stopCrawler() {
        boolean stop = crawler.stop();
        return ResultDTO.ok(stop);
    }


    /**
     * 状态
     */
    @GetMapping("/status")
    public ResultDTO<Crawler.Status> statusCrawler() {
        Crawler.Status status = crawler.getStatus();
        return ResultDTO.ok(status);
    }

    /**
     * 消费进度
     */
    @GetMapping("/progress")
    public ResultDTO<ConsumerProgressBO> getConsumerProgress() {
        if (scheduler instanceof MonitorableScheduler) {
            MonitorableScheduler monitorableScheduler = (MonitorableScheduler) scheduler;
            return ResultDTO.ok(new ConsumerProgressBO(monitorableScheduler.getLeftRequestsCount(crawler),
                    monitorableScheduler.getTotalRequestsCount(crawler)));
        }
        return ResultDTO.fail(SCHEDULE_IS_NOT_MONITOR);
    }

    /**
     * 重置消费
     */
    @GetMapping("/reset")
    public ResultDTO<?> resetConsumerProgress() {
        if (scheduler instanceof DuplicateRemover) {
            ((DuplicateRemover) scheduler).resetDuplicateCheck(crawler);
            return ResultDTO.ok();
        }
        return ResultDTO.fail(SCHEDULE_CANNOT_RESET);
    }

}
