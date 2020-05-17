package com.wxl.crawlerdytt.pipeline;

import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by wuxingle on 2020/5/17
 * es的索引管理
 */
public class EsIndexManager {

    private Map<String, Index> indexMap = new ConcurrentHashMap<>();

    public Index getIndex(String category) {
        Index index = indexMap.get(category);
        Assert.notNull(index, "category: " + category + " can not get index");
        return index;
    }

    public void putIndex(String category, String index, String type) {
        indexMap.put(category, new Index(index, type));
    }

    public IndexRequest indexRequest(String category) {
        Index index = getIndex(category);
        return new IndexRequest(index.getIndex(), index.getType());
    }

    public UpdateRequest updateRequest(String category) {
        Index index = getIndex(category);
        UpdateRequest request = new UpdateRequest();
        request.index(index.getIndex());
        request.type(index.getType());
        return request;
    }

    @Data
    public static class Index {
        private String index;
        private String type;

        public Index(String index) {
            this(index, "_doc");
        }

        public Index(String index, String type) {
            this.index = index;
            this.type = type;
        }
    }

}
