package com.wxl.crawlerdytt.core.impl;

import com.wxl.crawlerdytt.core.HtmlDownLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Create by wuxingle on 2020/5/2
 * httpClient downloader
 */
@Slf4j
public class HttpClientDownLoader implements HtmlDownLoader {

    private CloseableHttpClient httpClient;

    private ResponseHandler<String> defaultHandler;

    public HttpClientDownLoader(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
        this.defaultHandler = new DefaultResponseHandler();
    }

    @Override
    public String download(String url) throws IOException {
        return download(url, defaultHandler);
    }

    @Override
    public String download(String url, ResponseHandler<String> handler) throws IOException {
        HttpGet get = new HttpGet(url);
        return httpClient.execute(get, handler);
    }


    public static class DefaultResponseHandler implements ResponseHandler<String> {
        @Override
        public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity);
                }
                log.warn("http response entity is empty!");
            } else {
                log.warn("http response status code is:{}", httpResponse.getStatusLine().getStatusCode());
            }
            return "";
        }
    }
}
