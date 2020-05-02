package com.wxl.crawlerdytt.core.impl;

import com.wxl.crawlerdytt.core.DyttCrawler;
import com.wxl.crawlerdytt.core.DyttUrl;
import com.wxl.crawlerdytt.core.HtmlDownLoader;
import com.wxl.crawlerdytt.frontier.Frontier;
import com.wxl.crawlerdytt.frontier.VisitedFrontier;
import com.wxl.crawlerdytt.handler.HtmlHandler;
import com.wxl.crawlerdytt.handler.HtmlHandlerMapping;
import com.wxl.crawlerdytt.handler.ResultHandler;
import com.wxl.crawlerdytt.handler.ResultHandlerMapping;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * Create by wuxingle on 2020/5/1
 * 默认爬取器
 */
@Slf4j
public class DefaultCrawler implements DyttCrawler {

    private HtmlDownLoader downLoader;

    private Frontier frontier;

    private VisitedFrontier visitedFrontier;

    private List<HtmlHandlerMapping> handlerMappings;

    private List<ResultHandlerMapping> resultHandlerMappings;

    @Override
    public void crawl() {
        DyttUrl url;
        do {
            url = frontier.next();
            if (url == null) {
                break;
            }
            visitedFrontier.add(url);
            String html = downHtml(url.getUrl());

            HtmlHandler handler = getHandler(url, html);

            if (handler == null) {
                log.warn("url:{} can not found handler!", url.getUrl());
                continue;
            }

            Object result = handler.handle(url, html);

            ResultHandler resultHandler = getResultHandler(result);

            if (resultHandler == null) {
                log.warn("result:{} can not found result handler!", result);
                continue;
            }

            resultHandler.handle(url, result);

        } while (true);
    }

    private HtmlHandler getHandler(DyttUrl url, String html) {
        for (HtmlHandlerMapping handlerMapping : handlerMappings) {
            HtmlHandler handler = handlerMapping.getHandler(url, html);
            if (handler != null) {
                return handler;
            }
        }
        return null;
    }

    private ResultHandler getResultHandler(Object result) {
        for (ResultHandlerMapping resultHandlerMapping : resultHandlerMappings) {
            ResultHandler resultHandler = resultHandlerMapping.getResultHandler(result);
            if (resultHandler != null) {
                return resultHandler;
            }
        }
        return null;
    }

    private String downHtml(String url) {
        try {
            return downLoader.download(url);
        } catch (IOException e) {
            log.error("down load io error:{}", url, e);
        }
        return "";
    }

}









