package com.wxl.crawlerdytt.web.controller;

import com.wxl.crawlerdytt.properties.CrawlerProperties;
import com.wxl.crawlerdytt.web.dto.ResultDTO;
import jdk.nashorn.internal.objects.annotations.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.codecraft.webmagic.Spider;

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
    private CrawlerProperties crawlerProperties;

    @Autowired
    private ApplicationContext applicationContext;


    @GetMapping("/start")
    public ResultDTO<Boolean> startCrawler(){


        return ResultDTO.ok();
    }







}
