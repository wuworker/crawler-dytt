package com.wxl.dyttcrawler.web.controller

import com.wxl.dyttcrawler.domain.DyttMovie
import com.wxl.dyttcrawler.web.dto.Page
import com.wxl.dyttcrawler.web.dto.ResultCode
import com.wxl.dyttcrawler.web.dto.ResultDTO
import com.wxl.dyttcrawler.web.dto.search.DyttQuery
import com.wxl.dyttcrawler.web.dto.search.DyttSimpleMovie
import com.wxl.dyttcrawler.web.service.DyttSearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Create by wuxingle on 2021/10/12
 * 电影搜索
 */
@RestController
@RequestMapping("/dytt/search")
class DyttSearchController {

    @Autowired
    lateinit var dyttSearchService: DyttSearchService

    /**
     * 搜索
     */
    @PostMapping("")
    fun searchMovie(@RequestBody query: DyttQuery): ResultDTO<Page<DyttSimpleMovie>> {
        query.initFromSize()
        val page = dyttSearchService.searchDyttMovie(query)
        return ResultDTO.ok(page)
    }

    /**
     * 根据id搜索
     */
    @GetMapping("/{id}")
    fun searchById(@PathVariable("id") id: String): ResultDTO<DyttMovie> {
        val movie = dyttSearchService.searchById(id) ?: return ResultDTO.fail(ResultCode.RESULT_NOT_FOUND)
        return ResultDTO.ok(movie)
    }
}