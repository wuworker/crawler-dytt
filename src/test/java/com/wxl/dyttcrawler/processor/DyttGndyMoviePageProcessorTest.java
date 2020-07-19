package com.wxl.dyttcrawler.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wxl.dyttcrawler.domain.DyttMovie;
import org.junit.Test;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.selector.PlainText;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 * Create by wuxingle on 2020/5/10
 */
public class DyttGndyMoviePageProcessorTest {


    @Test
    public void test1() throws IOException {
        process("https://www.dytt8.net/html/gndy/dyzz/20200506/59996.html",
                "src/test/resources/detail1.html");
    }

    @Test
    public void test2() throws IOException {
        process("https://www.dytt8.net/html/gndy/dyzz/20200608/60105.html",
                "src/test/resources/detail2.html");

    }


    private void process(String urlstr, String html) throws IOException {
        Request request = new Request();
        request.setUrl(urlstr);
        request.setCharset("utf-8");
        request.setMethod("get");

        Page page = new Page();
        byte[] bytes = Files.readAllBytes(Paths.get(html));
        page.setBytes(bytes);
        page.setRequest(request);
        page.setUrl(new PlainText(urlstr));
        page.setDownloadSuccess(true);
        page.setCharset("utf-8");
        page.setRawText(new String(bytes, "utf-8"));
        page.setStatusCode(200);

        DyttGndyMoviePageProcessor processor = new DyttGndyMoviePageProcessor((p, url) -> 10, url -> true);

        processor.process(page);

        ResultItems resultItems = page.getResultItems();
        DyttMovie movie = resultItems.get(DyttMovie.class.getName());
        ObjectMapper mapper = new ObjectMapper();
        mapper.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(movie));
        System.out.println(movie.getDesc());
    }
}