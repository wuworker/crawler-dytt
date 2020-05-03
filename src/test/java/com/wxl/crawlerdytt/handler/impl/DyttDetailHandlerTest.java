package com.wxl.crawlerdytt.handler.impl;

import com.wxl.crawlerdytt.core.DyttUrl;
import com.wxl.crawlerdytt.core.impl.HttpClientDownLoader;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.io.IOException;

/**
 * Create by wuxingle on 2020/5/2
 */
public class DyttDetailHandlerTest {

    @Test
    public void test1() throws IOException {
        DyttUrl url = DyttUrl.builder()
                .url("https://www.dytt8.net/html/gndy/jddy/20200416/59940.html")
                .path("/html/gndy/jddy/20200416/59940.html")
                .layer(1)
                .build();

        HttpClientDownLoader downLoader = new HttpClientDownLoader(HttpClients.createDefault());

        String html = downLoader.download(url.getUrl());

        DyttDetailHandler dyttDetailHandler = new DyttDetailHandler();

        Object result = dyttDetailHandler.handle(url, html);

        System.out.println(result);
    }
}
