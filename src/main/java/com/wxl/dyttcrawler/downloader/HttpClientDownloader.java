package com.wxl.dyttcrawler.downloader;

import com.google.common.collect.ImmutableList;
import com.wxl.dyttcrawler.core.CrawlerListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.downloader.HttpClientRequestContext;
import us.codecraft.webmagic.downloader.HttpUriRequestConverter;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyProvider;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.utils.CharsetUtils;
import us.codecraft.webmagic.utils.HttpClientUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by wuxingle on 2020/5/10
 * httpClient下载
 */
@Slf4j
public class HttpClientDownloader implements Downloader {

    private final Map<String, CloseableHttpClient> httpClients = new HashMap<>();

    private HttpClientGenerator httpClientGenerator;

    private HttpUriRequestConverter httpUriRequestConverter;

    private ProxyProvider proxyProvider;

    private boolean responseHeader;

    private String defaultCharset;

    private List<CrawlerListener> crawlerListeners;

    HttpClientDownloader(HttpClientGenerator httpClientGenerator,
                         HttpUriRequestConverter httpUriRequestConverter,
                         ProxyProvider proxyProvider,
                         boolean responseHeader,
                         String defaultCharset,
                         List<CrawlerListener> crawlerListeners) {
        this.httpClientGenerator = httpClientGenerator;
        this.httpUriRequestConverter = httpUriRequestConverter;
        this.proxyProvider = proxyProvider;
        this.responseHeader = responseHeader;
        this.defaultCharset = defaultCharset;
        if (!CollectionUtils.isEmpty(crawlerListeners)) {
            this.crawlerListeners = ImmutableList.copyOf(crawlerListeners);
        }
    }


    @Override
    public Page download(Request request, Task task) {
        if (task == null || task.getSite() == null) {
            throw new NullPointerException("task or site can not be null");
        }
        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = getHttpClient(task.getSite());
        Proxy proxy = proxyProvider != null ? proxyProvider.getProxy(task) : null;
        HttpClientRequestContext requestContext = httpUriRequestConverter.convert(request, task.getSite(), proxy);
        Page page = Page.fail();
        try {
            httpResponse = httpClient.execute(requestContext.getHttpUriRequest(), requestContext.getHttpClientContext());
            page = handleResponse(request, request.getCharset() != null ? request.getCharset() : task.getSite().getCharset(), httpResponse, task);
            onSuccess(request, task);
            log.info("downloading page success {}", request.getUrl());
            return page;
        } catch (IOException e) {
            log.warn("download page {} error", request.getUrl(), e);
            onError(request, task);
            return page;
        } finally {
            if (httpResponse != null) {
                //ensure the connection is released back to pool
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            }
            if (proxyProvider != null && proxy != null) {
                proxyProvider.returnProxy(proxy, page, task);
            }
        }
    }

    @Override
    public void setThread(int thread) {
        httpClientGenerator.setPoolSize(thread);
    }

    protected Page handleResponse(Request request, String charset, HttpResponse httpResponse, Task task) throws IOException {
        byte[] bytes = EntityUtils.toByteArray(httpResponse.getEntity());
        String contentType = httpResponse.getEntity().getContentType() == null ? "" : httpResponse.getEntity().getContentType().getValue();
        Page page = new Page();
        page.setBytes(bytes);
        if (!request.isBinaryContent()) {
            if (charset == null) {
                charset = getHtmlCharset(contentType, bytes);
            }
            page.setCharset(charset);
            page.setRawText(new String(bytes, charset));
        }
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        page.setDownloadSuccess(true);
        if (responseHeader) {
            page.setHeaders(HttpClientUtils.convertHeaders(httpResponse.getAllHeaders()));
        }
        return page;
    }

    private CloseableHttpClient getHttpClient(Site site) {
        String domain = site.getDomain();
        CloseableHttpClient httpClient = httpClients.get(domain);
        if (httpClient == null) {
            synchronized (this) {
                httpClient = httpClients.get(domain);
                if (httpClient == null) {
                    httpClient = httpClientGenerator.getClient(site);
                    httpClients.put(domain, httpClient);
                }
            }
        }
        return httpClient;
    }

    private String getHtmlCharset(String contentType, byte[] contentBytes) throws IOException {
        String charset = CharsetUtils.detectCharset(contentType, contentBytes);
        if (charset == null) {
            charset = defaultCharset;
            log.warn("Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()",
                    defaultCharset);
        }
        return charset;
    }

    protected void onSuccess(Request request, Task task) {
        if (CollectionUtils.isNotEmpty(crawlerListeners)) {
            for (CrawlerListener crawlerListener : crawlerListeners) {
                try {
                    crawlerListener.onSuccess(request, task);
                } catch (Exception e) {
                    log.error("download on success process error:{}", request, e);
                }
            }
        }
    }

    protected void onError(Request request, Task task) {
        if (CollectionUtils.isNotEmpty(crawlerListeners)) {
            for (CrawlerListener crawlerListener : crawlerListeners) {
                try {
                    crawlerListener.onError(request, task);
                } catch (Exception e) {
                    log.error("download on error process error:{}", request, e);
                }
            }
        }
    }
}
