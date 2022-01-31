package com.wxl.dyttcrawler.processor

import com.fasterxml.jackson.databind.ObjectMapper
import com.wxl.dyttcrawler.domain.DyttMovie
import com.wxl.dyttcrawler.downloadPage
import org.junit.jupiter.api.Test
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
            "https://www.dytt8.net/html/gndy/dyzz/20200506/59996.html"
        )
    }

    @Test
    fun test2() {
        process(
            "https://www.dytt8.net/html/gndy/dyzz/20200608/60105.html"
        )
    }

    @Test
    fun test3() {
        process(
            "https://www.dytt8.net/html/gndy/jddy/20220130/62267.html"
        )
    }

    @Test
    fun test4() {
        process("https://dydytt.net/gndy/dyzz/20220103/62184.html")
    }

    private fun process(urlStr: String) {
        val page = downloadPage(urlStr)
        println(page.html)

        val processor = DyttGndyMoviePageProcessor(
            { _, _ -> 10 }, { true }
        )

        processor.process(page)

        val resultItems = page.resultItems
        val movie = resultItems.get<DyttMovie>(DyttMovie::class.java.name)

        val mapper = ObjectMapper().apply {
            setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
        }

        if (movie != null) {
            println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(movie))
            println(movie.desc)
        }
    }


}