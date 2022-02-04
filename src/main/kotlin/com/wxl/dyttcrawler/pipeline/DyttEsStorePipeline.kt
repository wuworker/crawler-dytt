package com.wxl.dyttcrawler.pipeline

import com.fasterxml.jackson.databind.ObjectMapper
import com.wxl.dyttcrawler.core.ElasticConstants.DYTT_INDEX
import com.wxl.dyttcrawler.domain.DyttMovie
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.update.UpdateRequest
import org.elasticsearch.action.update.UpdateResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import us.codecraft.webmagic.ResultItems
import us.codecraft.webmagic.Task
import java.io.Closeable

/**
 * Create by wuxingle on 2021/10/11
 * 电影存储到es
 */
@Order(0)
@Component
class DyttEsStorePipeline(
    private val client: RestHighLevelClient,
    private val objectMapper: ObjectMapper
) : DyttPipeline<DyttMovie>(DyttMovie::class.java),
    Closeable {

    companion object {
        private val log = LoggerFactory.getLogger(DyttEsStorePipeline::class.java)
    }

    override fun process(obj: DyttMovie, resultItems: ResultItems, task: Task) {
        val doc = objectMapper.writer()
            .writeValueAsString(obj)

        val request = UpdateRequest(DYTT_INDEX, obj.id)
        request.doc(doc, XContentType.JSON)

        request.upsert(doc, XContentType.JSON)

        client.updateAsync(request, RequestOptions.DEFAULT, object : ActionListener<UpdateResponse> {
            override fun onResponse(response: UpdateResponse) {
            }

            override fun onFailure(e: Exception) {
                log.error("update index fail:{}", obj, e)
            }
        })
    }

    override fun close() {
        client.close()
    }
}