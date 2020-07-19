package com.wxl.dyttcrawler.web.controller;

import com.wxl.dyttcrawler.core.Crawler;
import com.wxl.dyttcrawler.properties.CrawlerProperties;
import com.wxl.dyttcrawler.web.dto.ResultDTO;
import com.wxl.dyttcrawler.web.dto.crawler.CrawlerProgress;
import com.wxl.dyttcrawler.web.dto.crawler.ManualUrl;
import com.wxl.dyttcrawler.web.service.DyttCrawlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import static com.wxl.dyttcrawler.web.dto.ResultCode.BAD_PARAMS;

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
    private DyttCrawlerService dyttCrawlerService;


    /**
     * 手动调用url
     */
    @PostMapping(value = "/manualUrl")
    public ResultDTO<Boolean> manualInvokeUrl(@RequestBody ManualUrl manualUrl) {
        if (!StringUtils.hasText(manualUrl.getUrl())) {
            return ResultDTO.fail(BAD_PARAMS);
        }
        dyttCrawlerService.manualInvokeUrl(manualUrl.getUrl());
        return ResultDTO.ok();
    }


    /**
     * 启动
     */
    @PostMapping("/start")
    public ResultDTO<Boolean> startCrawler() {
        boolean start = dyttCrawlerService.startCrawler();
        return ResultDTO.ok(start);
    }


    /**
     * 停止
     */
    @PostMapping("/stop")
    public ResultDTO<Boolean> stopCrawler() {
        boolean stop = dyttCrawlerService.stopCrawler();
        return ResultDTO.ok(stop);
    }


    /**
     * 状态
     */
    @GetMapping("/status")
    public ResultDTO<Crawler.Status> statusCrawler() {
        Crawler.Status status = dyttCrawlerService.statusCrawler();
        return ResultDTO.ok(status);
    }

    /**
     * 消费进度
     */
    @GetMapping("/progress")
    public ResultDTO<CrawlerProgress> getConsumerProgress() {
        CrawlerProgress crawlerProgress = dyttCrawlerService.getCrawlerProgress();
        return ResultDTO.ok(crawlerProgress);
    }

    /**
     * 重置消费
     */
    @PostMapping("/reset")
    public ResultDTO<Boolean> resetConsumerProgress() {
        boolean res = dyttCrawlerService.resetCrawlerProgress();
        return ResultDTO.ok(res);
    }

}
