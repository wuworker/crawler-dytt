package com.wxl.dyttcrawler.web.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wxl.dyttcrawler.core.ElasticConstants
import com.wxl.dyttcrawler.domain.DyttMovie
import com.wxl.dyttcrawler.web.dto.Page
import com.wxl.dyttcrawler.web.dto.search.DyttQuery
import com.wxl.dyttcrawler.web.dto.search.DyttSimpleMovie
import org.elasticsearch.action.get.GetRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Create by wuxingle on 2021/10/13
 * 电影搜索service
 */
@Service
class DyttSearchService(
    val client: RestHighLevelClient,
    val objectMapper: ObjectMapper
) {

    companion object {

        private val log = LoggerFactory.getLogger(this::class.java)

        private val SEARCH_FIELDS = arrayOf(
            "id", "title", "url", "picUrl", "name", "year", "originPlace", "category", "score", "tags"
        )
    }

    /**
     * 电影搜索
     */
    fun searchDyttMovie(query: DyttQuery): Page<DyttSimpleMovie> {
        val boolQueryBuilder = QueryBuilders.boolQuery().apply {

            // 标题,片名,译名
            if (!query.title.isNullOrBlank()) {
                must(QueryBuilders.multiMatchQuery(query.title, "title", "name", "translateNames"))
            }

            // year
            if (query.yearStart != null || query.yearEnd != null) {
                val rangeQueryBuilder = QueryBuilders.rangeQuery("year")
                if (query.yearStart != null) {
                    rangeQueryBuilder.gte(query.yearStart)
                }
                if (query.yearEnd != null) {
                    rangeQueryBuilder.lte(query.yearEnd)
                }
                must(rangeQueryBuilder)
            }

            // 产地
            if (!query.originPlace.isNullOrEmpty()) {
                must(QueryBuilders.termsQuery("originPlace", query.originPlace))
            }

            // 类别
            if (!query.category.isNullOrEmpty()) {
                must(QueryBuilders.termsQuery("category", query.category))
            }

            // 主演,导演,编剧
            if (!query.people.isNullOrBlank()) {
                must(QueryBuilders.multiMatchQuery(query.people, "act", "director", "screenwriter"))
            }

            // 标签
            if (!query.tags.isNullOrEmpty()) {
                must(QueryBuilders.termsQuery("tags", query.tags))
            }
        }

        val sourceBuilder = SearchSourceBuilder()
            .query(boolQueryBuilder)
            .fetchSource(SEARCH_FIELDS, null)
            .from(query.from!!)
            .size(query.size!!)

        val request = SearchRequest()
            .indices(ElasticConstants.DYTT_INDEX)
            .source(sourceBuilder)

        val response = client.search(request, RequestOptions.DEFAULT)
        if (log.isDebugEnabled) {
            log.debug("movie search result:{}", response)
        }

        val totalHits = response.hits.totalHits?.value ?: 0
        val movies = response.hits.map {
            val source = it.sourceAsString
            objectMapper.readValue(source, DyttSimpleMovie::class.java)
        }

        return Page(movies, totalHits)
    }

    /**
     * 根据id获取详情
     */
    fun searchById(id: String): DyttMovie? {
        val request = GetRequest(ElasticConstants.DYTT_INDEX, id)

        val response = client.get(request, RequestOptions.DEFAULT)
        if (log.isDebugEnabled) {
            log.debug("movie get result:{}", response)
        }

        if (response.isExists) {
            val source = response.sourceAsString
            return objectMapper.readValue(source, DyttMovie::class.java)
        }

        return null
    }

}