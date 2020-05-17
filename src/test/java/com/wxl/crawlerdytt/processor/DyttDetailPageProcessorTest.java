package com.wxl.crawlerdytt.processor;

import com.wxl.crawlerdytt.core.DyttDetail;
import org.junit.Test;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.selector.PlainText;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Create by wuxingle on 2020/5/10
 */
public class DyttDetailPageProcessorTest {


    @Test
    public void test1() throws IOException {
        Request request = new Request();
        request.setUrl("https://www.dytt8.net/html/gndy/dyzz/20200506/59996.html");
        request.setCharset("utf-8");
        request.setMethod("get");

        Page page = new Page();
        byte[] bytes = Files.readAllBytes(Paths.get("src/test/resources/detail1.html"));
        page.setBytes(bytes);
        page.setRequest(request);
        page.setUrl(new PlainText("https://www.dytt8.net/html/gndy/dyzz/20200506/59996.html"));
        page.setDownloadSuccess(true);
        page.setCharset("utf-8");
        page.setRawText(new String(bytes, "utf-8"));
        page.setStatusCode(200);

        DyttDetailPageProcessor processor = new DyttDetailPageProcessor((p, url) -> 10, url -> true);

        processor.process(page);

        ResultItems resultItems = page.getResultItems();
        DyttDetail detail = resultItems.get(DyttDetail.class.getName());
        System.out.println(detail);
    }


}