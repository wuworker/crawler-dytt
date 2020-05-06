package com.wxl.crawlerdytt.core.impl;

import com.wxl.crawlerdytt.core.DyttConstants;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.io.IOException;

/**
 * Create by wuxingle on 2020/5/2
 */
public class HttpClientDownLoaderTest {


    @Test
    public void test1() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpClientDownLoader downLoader = new HttpClientDownLoader(httpClient);

        String download = downLoader.download("https://www.dytt8.net/", DyttConstants.DEFAULT_CHARSET);

        System.out.println(download);
    }
}