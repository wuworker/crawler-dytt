package com.wxl.crawlerdytt.core;

import org.apache.http.client.ResponseHandler;

import java.io.IOException;

/**
 * Create by wuxingle on 2020/5/2
 * 下载html
 */
public interface HtmlDownLoader {

    /**
     * 下载html
     *
     * @return html
     */
    String download(String url) throws IOException;


    String download(String url, ResponseHandler<String> handler) throws IOException;
}
