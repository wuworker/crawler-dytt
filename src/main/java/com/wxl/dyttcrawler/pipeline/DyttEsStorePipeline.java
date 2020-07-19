package com.wxl.dyttcrawler.pipeline;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.wxl.dyttcrawler.domain.DyttMovie;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

import java.io.Closeable;
import java.io.IOException;

import static com.wxl.dyttcrawler.core.DyttConstants.Elastic.DYTT_INDEX;
import static com.wxl.dyttcrawler.core.DyttConstants.Elastic.DYTT_TYPE;

/**
 * Create by wuxingle on 2020/5/10
 * 电影存储到es
 */
@Slf4j
@Order(0)
@Component
public class DyttEsStorePipeline extends DyttPipeline<DyttMovie> implements Closeable {

    private RestHighLevelClient client;

    private ObjectMapper objectMapper;

    @Autowired
    public DyttEsStorePipeline(RestHighLevelClient client,
                               ObjectMapper objectMapper) {
        super(DyttMovie.class);
        this.client = client;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void process(DyttMovie movie, ResultItems resultItems, Task task) {
        String doc;
        try {
            doc = objectMapper.writer(SerializationFeature.WRITE_NULL_MAP_VALUES).writeValueAsString(movie);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        UpdateRequest request = new UpdateRequest(DYTT_INDEX, DYTT_TYPE, movie.getId());

        request.doc(doc, XContentType.JSON);
        request.upsert(doc, XContentType.JSON);

        client.updateAsync(request, RequestOptions.DEFAULT, new ActionListener<UpdateResponse>() {
            @Override
            public void onResponse(UpdateResponse updateResponse) {

            }

            @Override
            public void onFailure(Exception e) {
                log.error("update index fail:{}", movie, e);
            }
        });
    }

    @Override
    public void close() throws IOException {
        client.close();
    }

}
