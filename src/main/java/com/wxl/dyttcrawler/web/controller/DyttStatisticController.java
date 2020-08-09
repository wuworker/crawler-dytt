package com.wxl.dyttcrawler.web.controller;

import com.wxl.dyttcrawler.web.dto.ResultCode;
import com.wxl.dyttcrawler.web.dto.ResultDTO;
import com.wxl.dyttcrawler.web.dto.TermItem;
import com.wxl.dyttcrawler.web.dto.statistic.BaseStatCount;
import com.wxl.dyttcrawler.web.dto.statistic.StatDimension;
import com.wxl.dyttcrawler.web.dto.statistic.YearMonthCount;
import com.wxl.dyttcrawler.web.dto.statistic.YearPlaceCount;
import com.wxl.dyttcrawler.web.service.DyttStatisticService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
     * 获取基础数据
     * 总数,种类,产地,语言
     */
    @GetMapping("/base")
    public ResultDTO<BaseStatCount> getBaseStat() throws IOException {
        BaseStatCount baseStatCount = dyttStatisticService.getBaseStat();
        return ResultDTO.ok(baseStatCount);
    }


    /**
     * 按字段聚合
     */
    @GetMapping("/agg/{field}")
    public ResultDTO<TermItem<String, Long>> aggByField(@PathVariable("field") String field) throws IOException {
        switch (field) {
            case StatDimension.CATEGORY:
            case StatDimension.LANGUAGE:
            case StatDimension.PLACE:
            case StatDimension.YEAR:
                break;
            default:
                return ResultDTO.fail(ResultCode.BAD_PARAMS);
        }

        TermItem<String, Long> termItem = dyttStatisticService.aggByField(field);
        return ResultDTO.ok(termItem);
    }

    /**
     * 每月电影数量，按year分组
     */
    @GetMapping("/agg/month/year")
    public ResultDTO<List<YearMonthCount>> getMonthCountGroupByYear(
            @RequestParam(value = "years", required = false) String yearStr) throws IOException {
        List<Integer> years = parseYears(yearStr);

        List<YearMonthCount> yearMonthCounts = dyttStatisticService.getMonthCountGroupByYear(years);
        return ResultDTO.ok(yearMonthCounts);
    }


    /**
     * 地区电影数量，按year分组
     */
    @GetMapping("/agg/place/year")
    public ResultDTO<List<YearPlaceCount>> getPlaceCountGroupByYear(
            @RequestParam(value = "years", required = false) String yearStr) throws IOException {
        List<Integer> years = parseYears(yearStr);

        List<YearPlaceCount> yearPlaceCount = dyttStatisticService.getPlaceCountGroupByYear(years);
        return ResultDTO.ok(yearPlaceCount);
    }


    private List<Integer> parseYears(String yearStr) {
        if (StringUtils.isBlank(yearStr)) {
            return Collections.singletonList(LocalDate.now().getYear());
        }
        return Arrays.stream(yearStr.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
