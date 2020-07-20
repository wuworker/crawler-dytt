package com.wxl.dyttcrawler.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wxl.dyttcrawler.domain.DyttMovie;
import com.wxl.dyttcrawler.web.dto.Page;
import com.wxl.dyttcrawler.web.dto.search.DyttQuery;
import com.wxl.dyttcrawler.web.dto.search.DyttSimpleMovie;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.wxl.dyttcrawler.core.DyttConstants.Elastic.DYTT_INDEX;
import static com.wxl.dyttcrawler.core.DyttConstants.Elastic.DYTT_TYPE;

/**
 * Create by wuxingle on 2020/7/16
 * 电影搜索service
 */
@Slf4j
@Service
public class DyttSearchService {

    private static final String[] SEARCH_FIELDS = new String[]{
            "id", "title", "url", "picUrl", "name", "year", "originPlace", "category", "score", "tags"
    };

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 电影搜索
     */
    public Page<DyttSimpleMovie> searchDyttMovie(DyttQuery query) throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 标题,片名,译名
        if (StringUtils.isNotBlank(query.getTitle())) {
            boolQueryBuilder.must(QueryBuilders.multiMatchQuery(query.getTitle(),
                    "title", "name", "translateNames"));
        }

        // year
        if (CollectionUtils.isNotEmpty(query.getYear())) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("year", query.getYear().toArray()));
        }

        // 产地
        if (CollectionUtils.isNotEmpty(query.getOriginPlace())) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("originPlace", query.getOriginPlace().toArray()));
        }

        // 类别
        if (CollectionUtils.isNotEmpty(query.getCategory())) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("category", query.getCategory().toArray()));
        }

        // 主演,导演,编剧
        if (StringUtils.isNotEmpty(query.getPeople())) {
            boolQueryBuilder.must(QueryBuilders.multiMatchQuery(query.getTitle(),
                    "act", "director", "screenwriter"));
        }

        // 标签
        if (CollectionUtils.isNotEmpty(query.getTags())) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("tags", query.getTags().toArray()));
        }

        sourceBuilder.query(boolQueryBuilder)
                .fetchSource(SEARCH_FIELDS, null)
                .from(query.getFrom())
                .size(query.getSize());

        SearchRequest request = new SearchRequest()
                .indices(DYTT_INDEX)
                .types(DYTT_TYPE)
                .source(sourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        if (log.isDebugEnabled()) {
            log.debug("movie search result:{}", response);
        }

        List<DyttSimpleMovie> movies = new ArrayList<>();

        SearchHits hits = response.getHits();
        long totalHits = hits.getTotalHits();
        for (SearchHit hit : hits.getHits()) {
            String source = hit.getSourceAsString();
            movies.add(objectMapper.readValue(source, DyttSimpleMovie.class));

        }

        return new Page<>(movies, (int) totalHits);
    }

    /**
     * 根据id获取详情
     */
    public DyttMovie searchById(String id) throws IOException {
        GetRequest request = new GetRequest(DYTT_INDEX, DYTT_TYPE, id);

        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        if (log.isDebugEnabled()) {
            log.debug("movie get result:{}", response);
        }

        if (response.isExists()) {
            String source = response.getSourceAsString();
            return objectMapper.readValue(source, DyttMovie.class);
        }

        return null;
    }
}
