package com.wxl.dyttcrawler.web.controller;

import com.wxl.dyttcrawler.web.dto.ResultDTO;
import com.wxl.dyttcrawler.web.dto.statistic.*;
import com.wxl.dyttcrawler.web.service.DyttStatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Create by wuxingle on 2020/7/12
 * 电影统计数据
 */
@Slf4j
@RestController
@RequestMapping("/dytt/statistic")
public class DyttStatisticController {

    @Autowired
    private DyttStatisticService dyttStatisticService;


    /**
     * 获取年份对应的电影数
     *
     * @return y1今年数, y3近3年, y5近5年, y全部
     */
    @GetMapping("/year/count")
    public ResultDTO<YearCount> getYearCount() throws IOException {
        YearCount yearCount = dyttStatisticService.getYearCount();
        return ResultDTO.ok(yearCount);
    }

    /**
     * 获取种类对应的电影数
     */
    @GetMapping("/category/count")
    public ResultDTO<CategoryCount> getCategoryCount() throws IOException {
        CategoryCount categoryCount = dyttStatisticService.getCategoryCount();
        return ResultDTO.ok(categoryCount);
    }

    /**
     * 基数统计
     * 种类,产地,语言
     */
    @GetMapping("/base/count")
    public ResultDTO<StatisticCardinality> getStatisticCardinality() throws IOException {
        StatisticCardinality statisticCardinality = dyttStatisticService.getStatisticCardinality();
        return ResultDTO.ok(statisticCardinality);
    }

    /**
     * 每月电影数量，按year分组
     */
    @GetMapping("/year/month/count")
    public ResultDTO<YearMonthCount> getMonthCountGroupByYear() throws IOException {
        YearMonthCount yearMonthCount = dyttStatisticService.getMonthCountGroupByYear();
        return ResultDTO.ok(yearMonthCount);
    }


    /**
     * 地区电影数量，按year分组
     */
    @GetMapping("/year/place/count")
    public ResultDTO<YearPlaceCount> getPlaceCountGroupByYear() throws IOException {
        YearPlaceCount yearPlaceCount = dyttStatisticService.getPlaceCountGroupByYear();
        return ResultDTO.ok(yearPlaceCount);
    }

}
