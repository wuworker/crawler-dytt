package com.wxl.dyttcrawler.core;

import org.junit.jupiter.api.Test;
import us.codecraft.webmagic.utils.UrlUtils;

import java.util.regex.Matcher;

import static com.wxl.dyttcrawler.core.DyttConstants.*;

/**
 * Create by wuxingle on 2020/7/19
 */
public class DyttConstantsTest {


    @Test
    public void test() throws Exception {
        System.out.println(DOMAIN_PATTERN.matcher("www.dytt8.net").matches());
        System.out.println(DOMAIN_PATTERN.matcher("www.ygdy8.com").matches());
        System.out.println(DOMAIN_PATTERN.matcher("www,ygdy8.com").matches());

        System.out.println("-----------------------");
        System.out.println(CATEGORY_LIST_PATH_PATTERN.matcher("/html/gndy/oumei/index.html").matches());
        System.out.println(CATEGORY_LIST_PATH_PATTERN.matcher("/html/gndy/oumei/").matches());
        System.out.println(CATEGORY_LIST_PATH_PATTERN.matcher("/html/gndy/oumei").matches());
        System.out.println(CATEGORY_LIST_PATH_PATTERN.matcher("/html/gndy/oumeiindex.html").matches());


        String s = UrlUtils.canonicalizeUrl("https://www.baidu.com/deff/gg", "http://taobao.com/abc");
        System.out.println(s);

        String pattern = GNDY_DETAIL_PATH_PATTERN.pattern();
        System.out.println(pattern);
        Matcher matcher = GNDY_DETAIL_PATH_PATTERN.matcher("https://www.dytt8.net/html/gndy/jddy/20160320/50523.html");
       if(matcher.find()){
           System.out.println(matcher.group(1));
           System.out.println(matcher.group(2));
           System.out.println(matcher.group(3));
           System.out.println(matcher.group(4));
       }

    }
}