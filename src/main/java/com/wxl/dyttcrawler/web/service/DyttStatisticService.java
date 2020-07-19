package com.wxl.dyttcrawler.web.service;

import com.wxl.dyttcrawler.web.dto.statistic.*;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static com.wxl.dyttcrawler.core.DyttConstants.Elastic.DYTT_INDEX;
import static com.wxl.dyttcrawler.core.DyttConstants.Elastic.DYTT_TYPE;

/**
 * Create by wuxingle on 2020/7/12
 * 电影统计数据service
 */
@Slf4j
@Service
public class DyttStatisticService {

    @Autowired
    private RestHighLevelClient client;

    /**
     * 年份聚合
     *
     * @return y1今年数, y3近3年, y5近5年, y全部
     */
    public YearCount getYearCount() throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        int year = LocalDate.now().getYear();
        RangeAggregationBuilder aggregationBuilder = AggregationBuilders.range("years")
                .field("year")
                .addUnboundedFrom("y1", year)
                .addUnboundedFrom("y3", year - 3)
                .addUnboundedFrom("y5", year - 5)
                .addUnboundedTo("y", year + 1);

        sourceBuilder.size(0).aggregation(aggregationBuilder);

        SearchRequest request = new SearchRequest()
                .indices(DYTT_INDEX)
                .types(DYTT_TYPE)
                .source(sourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        if (log.isDebugEnabled()) {
            log.debug("get year count:{}", response);
        }

        Range range = response.getAggregations().get("years");


        YearCount yearCount = new YearCount();
        for (Range.Bucket bucket : range.getBuckets()) {
            yearCount.add(bucket.getKeyAsString(), (int) bucket.getDocCount());
        }

        return yearCount;
    }

    /**
     * 种类聚合
     */
    public CategoryCount getCategoryCount() throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("categories")
                .field("category")
                .size(10);

        sourceBuilder.size(0).aggregation(aggregationBuilder);

        SearchRequest request = new SearchRequest()
                .indices(DYTT_INDEX)
                .types(DYTT_TYPE)
                .source(sourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        if (log.isDebugEnabled()) {
            log.debug("get category count:{}", response);
        }

        Terms terms = response.getAggregations().get("categories");

        CategoryCount categoryCount = new CategoryCount();
        for (Terms.Bucket bucket : terms.getBuckets()) {
            categoryCount.add(bucket.getKeyAsString(), (int) bucket.getDocCount());
        }
        return categoryCount;
    }

    /**
     * 基数统计
     * 种类,产地,语言
     */
    public StatisticCardinality getStatisticCardinality() throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.aggregation(AggregationBuilders.cardinality("categorySize").field("category"))
                .aggregation(AggregationBuilders.cardinality("placeSize").field("originPlace"))
                .aggregation(AggregationBuilders.cardinality("languageSize").field("language"))
                .size(0);

        SearchRequest request = new SearchRequest()
                .indices(DYTT_INDEX)
                .types(DYTT_TYPE)
                .source(sourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        if (log.isDebugEnabled()) {
            log.debug("get statistic cardinality result:{}", response);
        }

        Aggregations aggregations = response.getAggregations();
        int categorySize = (int) ((Cardinality) aggregations.get("categorySize")).getValue();
        int placeSize = (int) ((Cardinality) aggregations.get("placeSize")).getValue();
        int languageSize = (int) ((Cardinality) aggregations.get("languageSize")).getValue();

        return new StatisticCardinality(categorySize, placeSize, languageSize);
    }


    /**
     * 每月数量，按year分组
     */
    public YearMonthCount getMonthCountGroupByYear() throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        DateHistogramAggregationBuilder aggBuilder = AggregationBuilders.dateHistogram("years")
                .field("publishDate")
                .dateHistogramInterval(DateHistogramInterval.YEAR)
                .format("yyyy");

        DateHistogramAggregationBuilder subAggBuilder = AggregationBuilders.dateHistogram("months")
                .field("publishDate")
                .dateHistogramInterval(DateHistogramInterval.MONTH)
                .format("M");

        aggBuilder.subAggregation(subAggBuilder);

        sourceBuilder.aggregation(aggBuilder).size(0);

        SearchRequest request = new SearchRequest()
                .indices(DYTT_INDEX)
                .types(DYTT_TYPE)
                .source(sourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        if (log.isDebugEnabled()) {
            log.debug("get month count group by year result:{}", response);
        }

        Histogram yearHistogram = response.getAggregations().get("years");

        List<? extends Histogram.Bucket> yearBuckets = yearHistogram.getBuckets();

        YearMonthCount yearMonthCount = new YearMonthCount();
        for (Histogram.Bucket yearBucket : yearBuckets) {
            String key = yearBucket.getKeyAsString();

            Histogram monthHistogram = yearBucket.getAggregations().get("months");
            MonthCount monthCount = new MonthCount();
            for (Histogram.Bucket bucket : monthHistogram.getBuckets()) {
                monthCount.add(Integer.parseInt(bucket.getKeyAsString()), (int) bucket.getDocCount());
            }
            monthCount.fillMonth();

            yearMonthCount.add(key, monthCount);
        }

        return yearMonthCount;
    }


    /**
     * 地区数据，按year分组
     */
    public YearPlaceCount getPlaceCountGroupByYear() throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        DateHistogramAggregationBuilder aggBuilder = AggregationBuilders.dateHistogram("years")
                .field("publishDate")
                .dateHistogramInterval(DateHistogramInterval.YEAR)
                .format("yyyy");

        TermsAggregationBuilder subAggBuilder = AggregationBuilders.terms("places")
                .field("originPlace")
                .size(10);

        aggBuilder.subAggregation(subAggBuilder);

        sourceBuilder.aggregation(aggBuilder).size(0);

        SearchRequest request = new SearchRequest()
                .indices(DYTT_INDEX)
                .types(DYTT_TYPE)
                .source(sourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        if (log.isDebugEnabled()) {
            log.debug("get place count group by year result:{}", response);
        }

        Histogram yearHistogram = response.getAggregations().get("years");

        List<? extends Histogram.Bucket> yearBuckets = yearHistogram.getBuckets();

        YearPlaceCount yearPlaceCount = new YearPlaceCount();
        for (Histogram.Bucket yearBucket : yearBuckets) {
            String key = yearBucket.getKeyAsString();
            Terms terms = yearBucket.getAggregations().get("places");

            PlaceCount placeCount = new PlaceCount();
            for (Terms.Bucket bucket : terms.getBuckets()) {
                placeCount.add(bucket.getKeyAsString(), (int) bucket.getDocCount());
            }
            yearPlaceCount.add(key, placeCount);
        }

        return yearPlaceCount;
    }

}
