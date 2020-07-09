package com.wxl.crawlerdytt.pipeline;

import com.alibaba.fastjson.JSON;
import com.wxl.crawlerdytt.core.DyttDetail;
import com.wxl.crawlerdytt.properties.EsStoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

import java.io.Closeable;
import java.io.IOException;

/**
 * Create by wuxingle on 2020/5/10
 * 电影存储到es
 */
@Slf4j
@Order(0)
@Component
@EnableConfigurationProperties(EsStoreProperties.class)
public class DyttEsStorePipeline extends DyttPipeline<DyttDetail> implements Closeable {

    private RestHighLevelClient client;

    private String index;

    private String type;

    @Autowired
    public DyttEsStorePipeline(RestHighLevelClient client,
                               EsStoreProperties esStoreProperties) {
        super(DyttDetail.class);
        EsStoreProperties.DyttDetailIndexProperties detail = esStoreProperties.getDetail();

        this.client = client;
        this.index = detail.getIndex();
        this.type = detail.getType();
    }

    @Override
    protected void process(DyttDetail dyttDetail, ResultItems resultItems, Task task) {
        UpdateRequest request = getUpdateRequest(dyttDetail.getId());

        String doc = JSON.toJSONString(dyttDetail);
        request.doc(doc, XContentType.JSON);
        request.upsert(doc, XContentType.JSON);

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

    @Override
    public void close() throws IOException {
        client.close();
    }

    private UpdateRequest getUpdateRequest(String id) {
        return new UpdateRequest(index, type, id);
    }
}
