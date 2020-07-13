package com.wxl.crawlerdytt.web.service;

import com.wxl.crawlerdytt.web.dto.statistic.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * Create by wuxingle on 2020/7/12
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DyttStatisticServiceTest {

    @Autowired
    private DyttStatisticService statisticService;

    @Test
    public void testGetYearCount() throws IOException {

        YearCount yearCount = statisticService.getYearCount();
        System.out.println(yearCount);
    }

    @Test
    public void testGetCategoryCount() throws IOException {
        CategoryCount categoryCount = statisticService.getCategoryCount();
        System.out.println(categoryCount);
    }

    @Test
    public void testGetStatisticCardinality() throws IOException {
        StatisticCardinality statisticCardinality = statisticService.getStatisticCardinality();
        System.out.println(statisticCardinality);
    }

    @Test
    public void testGetMonthCountGroupByYear() throws IOException {
        YearMonthCount yearMonthCount = statisticService.getMonthCountGroupByYear();
        System.out.println(yearMonthCount);
    }

    @Test
    public void testGetPlaceCountGroupByYear() throws IOException {
        YearPlaceCount yearPlaceCount = statisticService.getPlaceCountGroupByYear();
        System.out.println(yearPlaceCount);
    }
}
