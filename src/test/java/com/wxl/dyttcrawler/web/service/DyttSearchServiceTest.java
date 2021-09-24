package com.wxl.dyttcrawler.web.service;

import com.wxl.dyttcrawler.domain.DyttMovie;
import com.wxl.dyttcrawler.web.dto.Page;
import com.wxl.dyttcrawler.web.dto.search.DyttQuery;
import com.wxl.dyttcrawler.web.dto.search.DyttSimpleMovie;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

/**
 * Create by wuxingle on 2020/7/16
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DyttSearchServiceTest {

    @Autowired
    private DyttSearchService searchService;

    @Test
    public void testSearchDyttMovie() throws IOException {
        DyttQuery query = new DyttQuery();
        query.setFrom(0);
        query.setSize(5);
        query.setYearEnd(200);
        query.setYearStart(2019);
        query.setCategory(Lists.newArrayList("剧情"));
        query.setOriginPlace(Lists.newArrayList("中国大陆"));
        query.setTitle("追龙幽魂");

        Page<DyttSimpleMovie> page = searchService.searchDyttMovie(query);
        System.out.println(page);
    }

    @Test
    public void testSearchById() throws IOException {
        DyttMovie movie = searchService.searchById("2020022859756");
        System.out.println(movie);
    }
}
