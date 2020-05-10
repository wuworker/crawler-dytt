package com.wxl.crawlerdytt.processor;

import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * Create by wuxingle on 2020/5/10
 * 页面处理分发
 */
@Slf4j
public class ProcessorDispatcher implements PageProcessor {


    private Site site ;

    private List<DyttProcessor> processors;

    public ProcessorDispatcher(Site site, List<DyttProcessor> processors) {
        this.site = site;
        this.processors = processors;
    }


    @Override
    public void process(Page page) {
        DyttProcessor processor = getProcessor(page);
        if (processor != null) {
            processor.process(page);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }


    private DyttProcessor getProcessor(Page page) {
        for (DyttProcessor processor : processors) {
            if (processor.match(page)) {
                return processor;
            }
        }
        return null;
    }

}
