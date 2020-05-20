package com.wxl.crawlerdytt.pipeline;

import com.wxl.crawlerdytt.core.DyttDetail;
import com.wxl.crawlerdytt.properties.CrawlerProperties;
import com.wxl.crawlerdytt.utils.DyttUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Create by wuxingle on 2020/5/18
 * 停止任务
 */
@Slf4j
@Order
@Component
@EnableConfigurationProperties(CrawlerProperties.class)
public class StoppedPipeline extends DyttPipeline<DyttDetail> {

    private int maxSize;

    private AtomicInteger count = new AtomicInteger(0);

    public StoppedPipeline(CrawlerProperties properties) {
        super(DyttDetail.class);
        this.maxSize = properties.getUpdateSize();
    }

    @Override
    protected void process(DyttDetail detail, ResultItems resultItems, Task task) {
        if (count.incrementAndGet() >= maxSize) {
            log.info("crawler will stop, because update size is max:{}", maxSize);
            DyttUtils.stopCrawler();
        }
    }


}
