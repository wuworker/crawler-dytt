package com.wxl.crawlerdytt.frontier.local;

import com.wxl.crawlerdytt.core.DyttUrl;
import org.junit.Test;

/**
 * Create by wuxingle on 2020/5/2
 */
public class BloomVisitedFrontierTest {

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

        BloomVisitedFrontier frontier = new BloomVisitedFrontier();
        frontier.add(url1);
        frontier.add(url2);
        frontier.add(url3);
        frontier.add(url4);

        boolean contains = frontier.contains(DyttUrl.builder().path("/a5").build());
        System.out.println(contains);

        contains = frontier.contains(DyttUrl.builder().path("/a7").build());
        System.out.println(contains);
    }

}



