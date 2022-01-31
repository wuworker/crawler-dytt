package com.wxl.dyttcrawler.core

import com.wxl.dyttcrawler.core.DyttPattern.CATEGORY_LIST_PATH_PATTERN
import com.wxl.dyttcrawler.core.DyttPattern.GNDY_DETAIL_PATH_PATTERN
import org.junit.jupiter.api.Test
import us.codecraft.webmagic.utils.UrlUtils
import java.util.regex.Matcher

/**
 * Create by wuxingle on 2021/10/12
 *
 */
class DyttPatternTest {

    @Test
    fun test(){
        println("-----------------------")
        println(CATEGORY_LIST_PATH_PATTERN.matcher("/html/gndy/oumei/index.html").matches())
        println(CATEGORY_LIST_PATH_PATTERN.matcher("/html/gndy/oumei/").matches())
        println(CATEGORY_LIST_PATH_PATTERN.matcher("/html/gndy/oumei").matches())
        println(CATEGORY_LIST_PATH_PATTERN.matcher("/html/gndy/oumeiindex.html").matches())


        val s = UrlUtils.canonicalizeUrl("https://www.baidu.com/deff/gg", "http://taobao.com/abc")
        println(s)

        val pattern: String = GNDY_DETAIL_PATH_PATTERN.pattern()
        println(pattern)
        val matcher: Matcher =
            GNDY_DETAIL_PATH_PATTERN.matcher("https://www.dytt8.net/html/gndy/jddy/20160320/50523.html")
        if (matcher.find()) {
            println(matcher.group(1))
            println(matcher.group(2))
            println(matcher.group(3))
            println(matcher.group(4))
        }
    }
}