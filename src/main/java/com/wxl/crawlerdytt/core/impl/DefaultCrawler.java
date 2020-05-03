package com.wxl.crawlerdytt.core.impl;

import com.wxl.crawlerdytt.core.DyttCrawler;
import com.wxl.crawlerdytt.core.DyttUrl;
import com.wxl.crawlerdytt.core.HtmlDownLoader;
import com.wxl.crawlerdytt.frontier.Frontier;
import com.wxl.crawlerdytt.frontier.VisitedFrontier;
import com.wxl.crawlerdytt.handler.HtmlHandler;
import com.wxl.crawlerdytt.handler.HtmlResultHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * Create by wuxingle on 2020/5/1
 * 默认爬取器
 * face
 */
@Slf4j
@Data
public class DefaultCrawler implements DyttCrawler {

    private HtmlDownLoader downLoader;

    private Frontier frontier;

    private VisitedFrontier visitedFrontier;

    private List<HtmlHandler> htmlHandlers;

    private List<HtmlResultHandler> resultHandlers;

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

            HtmlResultHandler resultHandler = getResultHandler(result);

            if (resultHandler == null) {
                log.warn("result:{} can not found result handler!", result);
                continue;
            }

            resultHandler.handle(url, result);

        } while (true);
    }

    private HtmlHandler getHandler(DyttUrl url, String html) {
        for (HtmlHandler htmlHandler : htmlHandlers) {
            if (htmlHandler.support(url, html)) {
                return htmlHandler;
            }
        }
        return null;
    }

    private HtmlResultHandler getResultHandler(Object result) {
        for (HtmlResultHandler resultHandler : resultHandlers) {
            if (resultHandler.support(result)) {
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









