package com.wxl.dyttcrawler.pipeline;

import com.wxl.dyttcrawler.domain.DyttMovie;
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
public class LogPipeline extends DyttPipeline<DyttMovie> {

    public LogPipeline() {
        super(DyttMovie.class);
    }

    @Override
    protected void process(DyttMovie movie, ResultItems resultItems, Task task) {
        log.info("process result:{}, {}", movie.getUrl(), movie.getTitle());
    }
}
