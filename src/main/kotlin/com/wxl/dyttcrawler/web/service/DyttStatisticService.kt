package com.wxl.dyttcrawler.web.service

import com.wxl.dyttcrawler.core.ElasticConstants.DYTT_INDEX
import com.wxl.dyttcrawler.web.dto.*
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.filter.Filter
import org.elasticsearch.search.aggregations.bucket.filter.Filters
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import org.elasticsearch.search.aggregations.metrics.Cardinality
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Create by wuxingle on 2021/10/13
 * 电影统计数据service
 */
@Service
class DyttStatisticService(
    private val client: RestHighLevelClient
) {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    /**
     * 基础数据
     * 种类,产地,语言
     */
    fun getBaseStat(): BaseStatCount {
        val sourceBuilder = SearchSourceBuilder().apply {
            // 基数聚合
            aggregation(AggregationBuilders.cardinality("categorySize").field(StatDimension.CATEGORY))
            aggregation(AggregationBuilders.cardinality("placeSize").field(StatDimension.PLACE))
            aggregation(AggregationBuilders.cardinality("languageSize").field(StatDimension.LANGUAGE))
            size(0)
        }

        val request = SearchRequest()
            .indices(DYTT_INDEX)
            .source(sourceBuilder)

        val response = client.search(request, RequestOptions.DEFAULT)
        if (log.isDebugEnabled) {
            log.debug("get statistic cardinality result:{}", response)
        }
        val totalHits = response.hits.totalHits?.value ?: 0
        val aggregations = response.aggregations

        val categorySize = aggregations.get<Cardinality>("categorySize").value
        val placeSize = aggregations.get<Cardinality>("placeSize").value
        val languageSize = aggregations.get<Cardinality>("languageSize").value

        return BaseStatCount(totalHits, categorySize, placeSize, languageSize)
    }

    /**
     * 按字段聚合
     */
    fun aggByField(field: String): TermItem<String, Long> {
        val termsBuilder = AggregationBuilders.terms("result")
            .field(field)
            .size(10)

        val sourceBuilder = SearchSourceBuilder()
            .aggregation(termsBuilder)
            .size(0)

        val request = SearchRequest()
            .indices(DYTT_INDEX)
            .source(sourceBuilder)

        val response = client.search(request, RequestOptions.DEFAULT)
        if (log.isDebugEnabled) {
            log.debug("get category count:{}", response)
        }

        val terms = response.aggregations.get<Terms>("result")

        val sum = terms.sumOfOtherDocCounts
        val items = terms.buckets.map {
            Pair(it.keyAsString, it.docCount)
        }

        return TermItem(items, sum)
    }

    /**
     * 每月数量，按year分组
     */
    fun getMonthCountGroupByYear(years: List<Int>): List<YearMonthCount> {

        val sourceBuilder = SearchSourceBuilder().apply {
            // 按publishDate年份过滤
            val queryBuilders = years.map {
                QueryBuilders.rangeQuery("publishDate").gte(it).lt(it + 1)
            }.toTypedArray()

            val yearFilters = AggregationBuilders.filters("yearFilters", *queryBuilders)

            // 按月聚合
            val subAggBuilder = AggregationBuilders.dateHistogram("months")
                .field("publishDate")
                .calendarInterval(DateHistogramInterval.MONTH)
                .format("M")

            yearFilters.subAggregation(subAggBuilder)

            aggregation(yearFilters)
            size(0)
        }

        val request = SearchRequest()
            .indices(DYTT_INDEX)
            .source(sourceBuilder)

        val response = client.search(request, RequestOptions.DEFAULT)
        if (log.isDebugEnabled) {
            log.debug("get month count group by year result:{}", response)
        }

        val filters = response.aggregations.get<Filters>("yearFilters")

        val yearMonthCounts = mutableListOf<YearMonthCount>()
        for (i in 0 until filters.buckets.size) {
            val monthCounts = LinkedHashMap<Int, MonthCount>()
            val months = filters.buckets[i].aggregations.get<Histogram>("months")

            for (bucket in months.buckets) {
                val month = bucket.keyAsString.toInt()
                monthCounts[month] = MonthCount(month, bucket.docCount)
            }
            // 补充其他月份为0
            if (monthCounts.size < 12) {
                for (j in 1..12) {
                    monthCounts.putIfAbsent(j, MonthCount(j, 0))
                }
            }

            yearMonthCounts.add(YearMonthCount(years[i], monthCounts.values.toList()))
        }

        return yearMonthCounts
    }

    /**
     * 地区数据，按year分组
     */
    fun getPlaceCountGroupByYear(years: List<Int>): List<YearPlaceCount> {
        val sourceBuilder = SearchSourceBuilder().apply {
            // 年份过滤
            val filterAgg = AggregationBuilders.filter(
                "yearFilter",
                QueryBuilders.termsQuery("year", years)
            )

            // 先按year聚合
            val aggBuilder = AggregationBuilders.terms("years")
                .field("year")
                .size(years.size)

            // 再按place聚合
            val subAggBuilder = AggregationBuilders.terms("places")
                .field("originPlace")
                .size(10)

            filterAgg.subAggregation(aggBuilder)
            aggBuilder.subAggregation(subAggBuilder)

            aggregation(filterAgg)
            size(0)
        }

        val request = SearchRequest()
            .indices(DYTT_INDEX)
            .source(sourceBuilder)

        val response = client.search(request, RequestOptions.DEFAULT)
        if (log.isDebugEnabled) {
            log.debug("get place count group by year result:{}", response)
        }

        val filter = response.aggregations.get<Filter>("yearFilter")
        val yearsTerm = filter.aggregations.get<Terms>("years")

        val yearPlaceCounts = mutableListOf<YearPlaceCount>()
        for (bucket in yearsTerm.buckets) {
            val terms = bucket.aggregations.get<Terms>("places")

            val placeCounts = terms.buckets.map {
                PlaceCount(it.keyAsString, it.docCount)
            }

            yearPlaceCounts.add(YearPlaceCount(bucket.keyAsString, placeCounts))
        }

        return yearPlaceCounts
    }

}