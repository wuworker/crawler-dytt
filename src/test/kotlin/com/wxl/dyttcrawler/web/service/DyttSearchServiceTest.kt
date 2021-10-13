package com.wxl.dyttcrawler.web.service

import com.wxl.dyttcrawler.web.dto.search.DyttQuery
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Create by wuxingle on 2021/10/13
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest
class DyttSearchServiceTest {

    @Autowired
    lateinit var searchService: DyttSearchService

    @Test
    fun testSearchDyttMovie() {
        val query = DyttQuery().apply {
            from = 0
            size = 5
            yearStart = 2019
            yearEnd = 2020
            category = listOf("剧情")
            originPlace = listOf("中国大陆")
            title = "追龙幽魂"
        }
        val page = searchService.searchDyttMovie(query)
        println(page)
    }

    @Test
    fun testSearchById() {
        val movie = searchService.searchById("2020022859756")
        println(movie)
    }
}