package com.wxl.dyttcrawler.web.service;

import com.wxl.dyttcrawler.web.dto.Item;
import com.wxl.dyttcrawler.web.dto.TermItem;
import com.wxl.dyttcrawler.web.dto.statistic.*;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.Filters;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
     * 基础数据
     * 种类,产地,语言
     */
    public BaseStatCount getBaseStat() throws IOException {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 基数聚合
        sourceBuilder.aggregation(AggregationBuilders.cardinality("categorySize").field(StatDimension.CATEGORY))
                .aggregation(AggregationBuilders.cardinality("placeSize").field(StatDimension.PLACE))
                .aggregation(AggregationBuilders.cardinality("languageSize").field(StatDimension.LANGUAGE))
                .size(0);

        SearchRequest request = new SearchRequest()
                .indices(DYTT_INDEX)
                .types(DYTT_TYPE)
                .source(sourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        if (log.isDebugEnabled()) {
            log.debug("get statistic cardinality result:{}", response);
        }
        long totalHits = response.getHits().getTotalHits();

        Aggregations aggregations = response.getAggregations();
        long categorySize = ((Cardinality) aggregations.get("categorySize")).getValue();
        long placeSize = ((Cardinality) aggregations.get("placeSize")).getValue();
        long languageSize = ((Cardinality) aggregations.get("languageSize")).getValue();

        return new BaseStatCount(totalHits, categorySize, placeSize, languageSize);
    }

    /**
     * 按字段聚合
     */
    public TermItem<String, Long> aggByField(String field) throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        TermsAggregationBuilder termsBuilder = AggregationBuilders.terms("result")
                .field(field)
                .size(10);

        sourceBuilder.aggregation(termsBuilder).size(0);

        SearchRequest request = new SearchRequest()
                .indices(DYTT_INDEX)
                .types(DYTT_TYPE)
                .source(sourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        if (log.isDebugEnabled()) {
            log.debug("get category count:{}", response);
        }

        Terms terms = response.getAggregations().get("result");

        long sumOfOtherDocCounts = terms.getSumOfOtherDocCounts();
        List<Item<String, Long>> items = new ArrayList<>();
        for (Terms.Bucket bucket : terms.getBuckets()) {
            items.add(new Item<>(bucket.getKeyAsString(), bucket.getDocCount()));
        }

        return new TermItem<>(items, sumOfOtherDocCounts);
    }


    /**
     * 每月数量，按year分组
     */
    public List<YearMonthCount> getMonthCountGroupByYear(List<Integer> years) throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 按publishDate年份过滤
        QueryBuilder[] queryBuilders = years.stream()
                .map(year -> QueryBuilders.rangeQuery("publishDate").gte(year).lt(year + 1))
                .toArray(QueryBuilder[]::new);

        FiltersAggregationBuilder yearFilters = AggregationBuilders.filters("yearFilters", queryBuilders);

        // 按月聚合
        DateHistogramAggregationBuilder subAggBuilder = AggregationBuilders.dateHistogram("months")
                .field("publishDate")
                .dateHistogramInterval(DateHistogramInterval.MONTH)
                .format("M");

        yearFilters.subAggregation(subAggBuilder);

        sourceBuilder.aggregation(yearFilters).size(0);

        SearchRequest request = new SearchRequest()
                .indices(DYTT_INDEX)
                .types(DYTT_TYPE)
                .source(sourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        if (log.isDebugEnabled()) {
            log.debug("get month count group by year result:{}", response);
        }

        Filters filters = response.getAggregations().get("yearFilters");

        List<YearMonthCount> yearMonthCounts = new ArrayList<>(filters.getBuckets().size());
        for (int i = 0; i < filters.getBuckets().size(); i++) {
            Map<Integer, MonthCount> monthCounts = new LinkedHashMap<>();
            Histogram months = filters.getBuckets().get(i).getAggregations().get("months");

            for (Histogram.Bucket bucket : months.getBuckets()) {
                int month = Integer.parseInt(bucket.getKeyAsString());
                monthCounts.put(month, new MonthCount(month, bucket.getDocCount()));
            }
            // 补充其他月份为0
            if (monthCounts.size() < 12) {
                for (int j = 1; j <= 12; j++) {
                    monthCounts.putIfAbsent(j, new MonthCount(j, 0L));
                }
            }

            yearMonthCounts.add(new YearMonthCount(years.get(i), new ArrayList<>(monthCounts.values())));
        }

        return yearMonthCounts;
    }


    /**
     * 地区数据，按year分组
     */
    public List<YearPlaceCount> getPlaceCountGroupByYear(List<Integer> years) throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 年份过滤
        FilterAggregationBuilder filterAgg = AggregationBuilders.filter("yearFilter",
                QueryBuilders.termsQuery("year", years));

        // 先按year聚合
        TermsAggregationBuilder aggBuilder = AggregationBuilders.terms("years")
                .field("year")
                .size(years.size());

        // 再按place聚合
        TermsAggregationBuilder subAggBuilder = AggregationBuilders.terms("places")
                .field("originPlace")
                .size(10);

        filterAgg.subAggregation(aggBuilder);
        aggBuilder.subAggregation(subAggBuilder);

        sourceBuilder.aggregation(filterAgg).size(0);

        SearchRequest request = new SearchRequest()
                .indices(DYTT_INDEX)
                .types(DYTT_TYPE)
                .source(sourceBuilder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        if (log.isDebugEnabled()) {
            log.debug("get place count group by year result:{}", response);
        }

        Filter filter = response.getAggregations().get("yearFilter");
        Terms yearsTerm = filter.getAggregations().get("years");

        List<YearPlaceCount> yearPlaceCounts = new ArrayList<>();
        for (Terms.Bucket bucket : yearsTerm.getBuckets()) {
            Terms aggregations = bucket.getAggregations().get("places");

            List<PlaceCount> placeCounts = new ArrayList<>();

            for (Terms.Bucket bk : aggregations.getBuckets()) {
                placeCounts.add(new PlaceCount(bk.getKeyAsString(), bk.getDocCount()));
            }

            yearPlaceCounts.add(new YearPlaceCount(bucket.getKeyAsString(), placeCounts));
        }

        return yearPlaceCounts;
    }

}
