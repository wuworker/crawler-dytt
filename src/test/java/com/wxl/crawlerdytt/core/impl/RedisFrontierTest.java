package com.wxl.crawlerdytt.core.impl;

import com.wxl.crawlerdytt.core.DyttUrl;
import com.wxl.crawlerdytt.frontier.Frontier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Create by wuxingle on 2020/5/1
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisFrontierTest {

    @Autowired
    private Frontier frontier;

    @Test
    public void test1() {
        DyttUrl url1 = DyttUrl.builder()
                .path("/a1")
                .layer(1)
                .build();
        DyttUrl url2 = DyttUrl.builder()
                .path("/a2")
                .layer(2)
                .build();
        DyttUrl url3 = DyttUrl.builder()
                .path("/a5")
                .layer(5)
                .build();
        DyttUrl url4 = DyttUrl.builder()
                .path("/a3")
                .layer(3)
                .build();

        frontier.add(url1);
        frontier.add(url2);
        frontier.add(url3);
        frontier.add(url4);

        List<DyttUrl> next = frontier.next(5);
        System.out.println(next);
    }

}