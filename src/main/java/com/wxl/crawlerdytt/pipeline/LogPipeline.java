package com.wxl.crawlerdytt.pipeline;

import com.wxl.crawlerdytt.core.DyttDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

/**
 * Create by wuxingle on 2020/5/10
 * 记录
 */
@Slf4j
@Component
public class LogPipeline extends DyttPipeline<DyttDetail> {

    public LogPipeline() {
        super(DyttDetail.class);
    }

    @Override
    protected void process(DyttDetail dyttDetail, ResultItems resultItems, Task task) {
        log.info("process result:{},{}", dyttDetail.getUrl(), dyttDetail.getTitle());
    }
}
