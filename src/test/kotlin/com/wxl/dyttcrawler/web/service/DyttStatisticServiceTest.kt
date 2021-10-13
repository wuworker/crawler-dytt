package com.wxl.dyttcrawler.web.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Create by wuxingle on 2020/7/12
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest
class DyttStatisticServiceTest {

    @Autowired
    lateinit var statisticService: DyttStatisticService

    @Test
    fun testAggByField() {
        val termItem = statisticService.aggByField("category")
        println(termItem)
    }

    @Test
    fun testGetBaseStat() {
        val baseStatCount = statisticService.getBaseStat()
        println(baseStatCount)
    }

    @Test
    fun testGetMonthCountGroupByYear() {
        val yearMonthCount = statisticService.getMonthCountGroupByYear(
            listOf(2018, 2020)
        )
        println(yearMonthCount)
    }

    @Test
    fun testGetPlaceCountGroupByYear() {
        val yearPlaceCounts = statisticService.getPlaceCountGroupByYear(listOf(2020))
        println(yearPlaceCounts)
    }
}
