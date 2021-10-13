package com.wxl.dyttcrawler.web.controller

import com.wxl.dyttcrawler.web.dto.*
import com.wxl.dyttcrawler.web.service.DyttStatisticService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

/**
 * Create by wuxingle on 2021/10/13
 * 电影统计数据
 */
@RestController
@RequestMapping("/dytt/statistic")
class DyttStatisticController {

    @Autowired
    lateinit var dyttStatisticService: DyttStatisticService

    /**
     * 获取基础数据
     * 总数,种类,产地,语言
     */
    @GetMapping("/base")
    fun getBaseStat(): ResultDTO<BaseStatCount> {
        val baseStatCount = dyttStatisticService.getBaseStat()
        return ResultDTO.ok(baseStatCount)
    }

    /**
     * 按字段聚合
     */
    @GetMapping("/agg/{field}")
    fun aggByField(@PathVariable("field") field: String): ResultDTO<TermItem<String, Long>> {
        when (field) {
            StatDimension.CATEGORY, StatDimension.LANGUAGE, StatDimension.PLACE, StatDimension.YEAR -> {
            }
            else -> return ResultDTO.fail(ResultCode.BAD_PARAMS)
        }
        val termItem = dyttStatisticService.aggByField(field)
        return ResultDTO.ok(termItem)
    }

    /**
     * 每月电影数量，按year分组
     */
    @GetMapping("/agg/month/year")
    fun getMonthCountGroupByYear(
        @RequestParam(value = "years", required = false) yearStr: String?
    ): ResultDTO<List<YearMonthCount>> {
        val years = parseYears(yearStr)
        val yearMonthCounts = dyttStatisticService.getMonthCountGroupByYear(years)
        return ResultDTO.ok(yearMonthCounts)
    }

    /**
     * 地区电影数量，按year分组
     */
    @GetMapping("/agg/place/year")
    fun getPlaceCountGroupByYear(
        @RequestParam(value = "years", required = false) yearStr: String?
    ): ResultDTO<List<YearPlaceCount>> {
        val years = parseYears(yearStr)
        val yearPlaceCount = dyttStatisticService.getPlaceCountGroupByYear(years)
        return ResultDTO.ok(yearPlaceCount)
    }

    private fun parseYears(yearStr: String?): List<Int> {
        if (yearStr.isNullOrBlank()) {
            return listOf(LocalDate.now().year)
        }
        return yearStr.split(",").map { it.toInt() }
    }
}
