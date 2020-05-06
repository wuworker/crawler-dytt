package com.wxl.crawlerdytt.core;

import org.apache.http.client.ResponseHandler;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
    String download(String url, Charset charset) throws IOException;


    String download(String url, ResponseHandler<String> handler) throws IOException;


    default String download(String url) throws IOException {
        return download(url, StandardCharsets.UTF_8);
    }

    default void downloadTo(String url, Charset charset, Writer out) throws IOException {
        String result = download(url, charset);

        out.write(result);
        out.flush();
    }
}
