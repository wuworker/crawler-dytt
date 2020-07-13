package com.wxl.crawlerdytt.es;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Create by wuxingle on 2020/7/12
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsClientTest {

    @Autowired
    private RestHighLevelClient client;

    @Test
    public void test1() throws Exception {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("years").field("year");
        sourceBuilder.aggregation(aggregationBuilder);
        sourceBuilder.size(0);

        SearchRequest request = new SearchRequest();
        request.indices("dytt");
        request.types("_doc");
        request.source(sourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        System.out.println(response);

        Aggregations aggregations = response.getAggregations();

        System.out.println(aggregations.asMap());

        Terms years = aggregations.get("years");

        System.out.println("-----------------------------------");
        List<? extends Terms.Bucket> buckets = years.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            System.out.println(bucket.getKeyAsString() + ":" + bucket.getDocCount());
        }
    }
}
