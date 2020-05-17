package com.wxl.crawlerdytt.pipeline;

import com.alibaba.fastjson.JSON;
import com.wxl.crawlerdytt.core.DyttDetail;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

/**
 * Create by wuxingle on 2020/5/10
 * 电影存储到es
 */
@Slf4j
@Component
public class DyttEsStorePipeline extends DyttPipeline<DyttDetail> {

    public static final String INDEX_CATEGORY = "dytt-detail";

    private RestHighLevelClient client;

    private EsIndexManager indexManager;

    @Autowired
    public DyttEsStorePipeline(RestHighLevelClient client,
                               EsIndexManager indexManager) {
        super(DyttDetail.class);
        this.client = client;
        this.indexManager = indexManager;

        // check
        indexManager.getIndex(INDEX_CATEGORY);
    }

    @Override
    protected void process(DyttDetail dyttDetail, ResultItems resultItems, Task task) {
        UpdateRequest request = indexManager.updateRequest(INDEX_CATEGORY);
        request.id(dyttDetail.getId());
        request.upsert(JSON.toJSONString(dyttDetail), XContentType.JSON);

        client.updateAsync(request, RequestOptions.DEFAULT, new ActionListener<UpdateResponse>() {
            @Override
            public void onResponse(UpdateResponse updateResponse) {

            }

            @Override
            public void onFailure(Exception e) {
                log.error("update index fail:{}", dyttDetail, e);
            }
        });
    }
}
