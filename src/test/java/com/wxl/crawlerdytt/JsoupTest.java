package com.wxl.crawlerdytt;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

/**
 * Create by wuxingle on 2020/5/1
 */
public class JsoupTest {

    @Test
    public void test1() throws IOException {
        Document document = Jsoup.connect("https://www.dytt8.net/").get();

        Elements select = document.select("a[href]");
        System.out.println(select);
    }
}
