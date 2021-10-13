package com.wxl.dyttcrawler.processor

import com.fasterxml.jackson.databind.ObjectMapper
import com.wxl.dyttcrawler.domain.DyttMovie
import org.junit.jupiter.api.Test
import us.codecraft.webmagic.Page
import us.codecraft.webmagic.Request
import us.codecraft.webmagic.selector.PlainText
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.time.ZoneId
import java.util.*

/**
 * Create by wuxingle on 2021/10/12
 *
 */
class DyttGndyMoviePageProcessorTest {

    @Test
    fun test1() {
        process(
            "https://www.dytt8.net/html/gndy/dyzz/20200506/59996.html",
            "src/test/resources/detail1.html"
        )
    }

    @Test
    fun test2() {
        process(
            "https://www.dytt8.net/html/gndy/dyzz/20200608/60105.html",
            "src/test/resources/detail2.html"
        )
    }

    private fun process(urlStr: String, html: String) {
        val req = Request().apply {
            url = urlStr
            charset = "utf-8"
            method = "get"
        }

        val page = Page().apply {
            val htmlBytes = Files.readAllBytes(Paths.get(html))

            bytes = htmlBytes
            request = req
            url = PlainText(urlStr)
            isDownloadSuccess = true
            charset = "utf-8"
            rawText = String(htmlBytes, Charset.forName("utf-8"))
            statusCode = 200
        }

        val processor = DyttGndyMoviePageProcessor(
            { _, _ -> 10 }, { true }
        )

        processor.process(page)

        val resultItems = page.resultItems
        val movie = resultItems.get<DyttMovie>(DyttMovie::class.java.name)

        val mapper = ObjectMapper().apply {
            setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
        }

        println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(movie))
        println(movie.desc)
    }


}