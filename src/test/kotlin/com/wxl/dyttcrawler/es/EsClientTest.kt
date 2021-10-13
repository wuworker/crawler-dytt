package com.wxl.dyttcrawler.es

import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Create by wuxingle on 2021/10/12
 *
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest
class EsClientTest {

    @Autowired
    lateinit var client: RestHighLevelClient

    @Test
    fun test1() {
        val sourceBuilder = SearchSourceBuilder()
        val aggregationBuilder = AggregationBuilders.terms("years").field("year")
        sourceBuilder.aggregation(aggregationBuilder)
        sourceBuilder.size(0)

        val request = SearchRequest()
        request.indices("dytt")
        request.source(sourceBuilder)

        val response = client.search(request, RequestOptions.DEFAULT)
        println(response)

        val agg = response.aggregations
        println(agg.asMap())

        val years = agg.get<Terms>("years")
        println("----------------------------")

        val buckets = years.buckets
        for (bucket in buckets) {
            println(bucket.keyAsString + ":" + bucket.docCount)
        }
    }


}