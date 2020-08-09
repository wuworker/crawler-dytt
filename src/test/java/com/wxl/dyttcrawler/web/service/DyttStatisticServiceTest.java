package com.wxl.dyttcrawler.web.service;

import com.wxl.dyttcrawler.web.dto.TermItem;
import com.wxl.dyttcrawler.web.dto.statistic.BaseStatCount;
import com.wxl.dyttcrawler.web.dto.statistic.YearMonthCount;
import com.wxl.dyttcrawler.web.dto.statistic.YearPlaceCount;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Create by wuxingle on 2020/7/12
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DyttStatisticServiceTest {

    @Autowired
    private DyttStatisticService statisticService;

    @Test
    public void testAggByField() throws IOException {
        TermItem<String, Long> termItem = statisticService.aggByField("category");
        System.out.println(termItem);
    }

    @Test
    public void testGetBaseStat() throws IOException {
        BaseStatCount baseStatCount = statisticService.getBaseStat();
        System.out.println(baseStatCount);
    }

    @Test
    public void testGetMonthCountGroupByYear() throws IOException {
        List<YearMonthCount> yearMonthCount = statisticService.getMonthCountGroupByYear(
                Lists.newArrayList(2018,2020));
        System.out.println(yearMonthCount);
    }

    @Test
    public void testGetPlaceCountGroupByYear() throws IOException {
        List<YearPlaceCount> yearPlaceCounts = statisticService.getPlaceCountGroupByYear(
                Collections.singletonList(2020));
        System.out.println(yearPlaceCounts);
    }
}
