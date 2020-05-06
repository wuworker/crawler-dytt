package com.wxl.crawlerdytt.core.impl;

import com.wxl.crawlerdytt.core.HtmlDownLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Create by wuxingle on 2020/5/2
 * httpClient downloader
 */
@Slf4j
public class HttpClientDownLoader implements HtmlDownLoader {

    private CloseableHttpClient httpClient;

    public HttpClientDownLoader(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public String download(String url, Charset charset) throws IOException {
        return download(url, response -> {
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity, charset);
                }
                log.warn("http response entity is empty!");
            } else {
                log.warn("http response status code is:{}", response.getStatusLine().getStatusCode());
            }
            return "";
        });
    }

    @Override
    public String download(String url, ResponseHandler<String> handler) throws IOException {
        HttpGet get = new HttpGet(url);
        return httpClient.execute(get, handler);
    }

}
