package com.wxl.crawlerdytt;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Test
    public void test2() {
        byte[] bytes = "　　　　　 ".getBytes();
        for (byte aByte : bytes) {
            String s = Integer.toHexString(aByte);
            System.out.println(s);
        }
        System.out.println();
    }
}
