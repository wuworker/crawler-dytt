package com.wxl.dyttcrawler.downloader;

import org.springframework.util.Assert;
import us.codecraft.webmagic.downloader.HttpUriRequestConverter;
import us.codecraft.webmagic.proxy.ProxyProvider;

/**
 * Create by wuxingle on 2020/5/10
 * downloader builder
 */
public class HttpClientDownloaderBuilder {

    private HttpClientGenerator httpClientGenerator;

    private HttpUriRequestConverter requestConverter;

    private ProxyProvider proxyProvider;

    private boolean responseHeader;

    private String defaultCharset;

    public HttpClientDownloaderBuilder httpClientGenerator(HttpClientGenerator generator) {
        this.httpClientGenerator = generator;
        return this;
    }


    public HttpClientDownloaderBuilder requestConverter(HttpUriRequestConverter converter) {
        this.requestConverter = converter;
        return this;
    }

    public HttpClientDownloaderBuilder proxyProvider(ProxyProvider proxyProvider) {
        this.proxyProvider = proxyProvider;
        return this;
    }

    public HttpClientDownloaderBuilder responseHeader(boolean responseHeader) {
        this.responseHeader = responseHeader;
        return this;
    }

    public HttpClientDownloaderBuilder defaultCharset(String defaultCharset) {
        this.defaultCharset = defaultCharset;
        return this;
    }

    public HttpClientDownloader build() {
        Assert.notNull(httpClientGenerator, "http client generator can not null");
        Assert.notNull(defaultCharset, "default charset can not null");
        if (requestConverter == null) {
            requestConverter = new HttpUriRequestConverter();
        }

        return new HttpClientDownloader(httpClientGenerator,
                requestConverter,
                proxyProvider,
                responseHeader,
                defaultCharset);
    }
}


