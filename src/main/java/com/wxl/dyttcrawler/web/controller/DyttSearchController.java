package com.wxl.dyttcrawler.web.controller;

import com.wxl.dyttcrawler.domain.DyttMovie;
import com.wxl.dyttcrawler.web.dto.Page;
import com.wxl.dyttcrawler.web.dto.ResultDTO;
import com.wxl.dyttcrawler.web.dto.search.DyttQuery;
import com.wxl.dyttcrawler.web.dto.search.DyttSimpleMovie;
import com.wxl.dyttcrawler.web.service.DyttSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.wxl.dyttcrawler.web.dto.ResultCode.RESULT_NOT_FOUND;

/**
 * Create by wuxingle on 2020/7/16
 * 电影搜索
 */
@Slf4j
@RestController
@RequestMapping("/dytt/search")
public class DyttSearchController {


    @Autowired
    private DyttSearchService dyttSearchService;

    /**
     * 搜索
     */
    @PostMapping("/")
    public ResultDTO<Page<DyttSimpleMovie>> searchMovie(@RequestBody DyttQuery query) throws IOException {
        query.initFromSize();
        Page<DyttSimpleMovie> page = dyttSearchService.searchDyttMovie(query);
        return ResultDTO.ok(page);
    }

    /**
     * 根据id搜索
     */
    @GetMapping("/{id}")
    public ResultDTO<DyttMovie> searchById(@PathVariable("id") String id) throws IOException {
        DyttMovie movie = dyttSearchService.searchById(id);
        if (movie == null) {
            return ResultDTO.fail(RESULT_NOT_FOUND);
        }
        return ResultDTO.ok(movie);
    }
}
