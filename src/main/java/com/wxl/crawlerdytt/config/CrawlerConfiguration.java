package com.wxl.crawlerdytt.config;

import com.wxl.crawlerdytt.core.DyttCrawler;
import com.wxl.crawlerdytt.core.HtmlDownLoader;
import com.wxl.crawlerdytt.core.impl.DefaultCrawler;
import com.wxl.crawlerdytt.frontier.Frontier;
import com.wxl.crawlerdytt.frontier.VisitedFrontier;
import com.wxl.crawlerdytt.handler.HtmlHandler;
import com.wxl.crawlerdytt.handler.HtmlResultHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Create by wuxingle on 2020/5/1
 * 基础配置
 */
@Slf4j
@Configuration
public class CrawlerConfiguration {

    @Autowired
    private HtmlDownLoader downLoader;

    @Autowired
    private Frontier frontier;

    @Autowired
    private VisitedFrontier visitedFrontier;


    @Autowired
    private List<HtmlHandler> htmlHandlers;

    @Autowired
    private List<HtmlResultHandler> resultHandlers;


    @Bean
    public DyttCrawler dyttCrawler() {
        DefaultCrawler crawler = new DefaultCrawler();
        crawler.setDownLoader(downLoader);
        crawler.setFrontier(frontier);
        crawler.setVisitedFrontier(visitedFrontier);
        crawler.setHtmlHandlers(htmlHandlers);
        crawler.setResultHandlers(resultHandlers);
        return crawler;
    }

}



