package com.wxl.crawlerdytt.handler.impl;

import com.wxl.crawlerdytt.core.DyttConstants;
import com.wxl.crawlerdytt.core.DyttUrl;
import com.wxl.crawlerdytt.core.impl.HttpClientDownLoader;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Create by wuxingle on 2020/5/2
 */
public class DyttDetailHandlerTest {

    @Test
    public void test0() throws IOException {
        DyttUrl url = DyttUrl.builder()
                .url("https://www.dytt8.net/html/gndy/jddy/20200416/59940.html")
                .path("/html/gndy/jddy/20200416/59940.html")
                .layer(1)
                .build();

        HttpClientDownLoader downLoader = new HttpClientDownLoader(HttpClients.createDefault());

        File file = new File("src/test/resources/detail1.html");
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), "utf-8")) {
            downLoader.downloadTo(url.getUrl(), Charset.forName("gbk"), writer);
        }
    }

    @Test
    public void test1() throws IOException {
        DyttUrl url = DyttUrl.builder()
                .url("https://www.dytt8.net/html/gndy/jddy/20200416/59940.html")
                .path("/html/gndy/jddy/20200416/59940.html")
                .layer(1)
                .build();

        byte[] bytes = Files.readAllBytes(Paths.get("src/test/resources/detail1.html"));
        String html = new String(bytes);


        DyttDetailHandler dyttDetailHandler = new DyttDetailHandler();

        Object result = dyttDetailHandler.handle(url, html);

        System.out.println(result);
    }

    @Test
    public void test2() throws IOException{
        DyttUrl url = DyttUrl.builder()
                .url("https://www.dytt8.net/html/gndy/dyzz/20200430/59973.html")
                .path("/html/gndy/jddy/20200416/59940.html")
                .layer(1)
                .build();

        HttpClientDownLoader downLoader = new HttpClientDownLoader(HttpClients.createDefault());

        String html = downLoader.download(url.getUrl(), DyttConstants.DEFAULT_CHARSET);

        DyttDetailHandler dyttDetailHandler = new DyttDetailHandler();

        Object result = dyttDetailHandler.handle(url, html);

        System.out.println(result);
    }
}
