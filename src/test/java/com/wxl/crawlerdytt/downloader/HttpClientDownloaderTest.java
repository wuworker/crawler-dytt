package com.wxl.crawlerdytt.downloader;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.Test;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpUriRequestConverter;
import us.codecraft.webmagic.selector.Html;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Create by wuxingle on 2020/7/8
 * -Djavax.net.debug=ssl,handshake
 */
public class HttpClientDownloaderTest {

    @Test
    public void test1() {
        Site site = new Site();
        site.setCharset("gbk");
        site.setDomain("www.dytt8.net");

        HttpClientDownloader downloader = getDownloader();
        Page page = downloader.download(new Request("https://www.dytt8.net/html/gndy/dyzz/20200426/59964.html"),
                new Task() {
                    @Override
                    public String getUUID() {
                        return "test";
                    }

                    @Override
                    public Site getSite() {
                        return site;
                    }
                });
        Html html = page.getHtml();
        System.out.println(html);

    }

    private HttpClientDownloader getDownloader() {

        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", sslConnectionSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                reg, null, null, null,
                60, TimeUnit.SECONDS);
        connectionManager.setMaxTotal(10);
        connectionManager.setDefaultMaxPerRoute(10);

        HttpClientGenerator httpClientGenerator = new HttpClientGenerator(connectionManager);

        return new HttpClientDownloaderBuilder()
                .httpClientGenerator(httpClientGenerator)
                .requestConverter(new HttpUriRequestConverter())
                .responseHeader(false)
                .defaultCharset("gbk")
                .build();
    }


    private SSLConnectionSocketFactory sslConnectionSocketFactory() {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(TrustAllStrategy.INSTANCE);
            SSLContext sslContext = builder.build();

            SSLSocket socket = (SSLSocket) sslContext.getSocketFactory().createSocket();
            String[] enabledProtocols = socket.getEnabledProtocols();
            String[] enabledCipherSuites = socket.getEnabledCipherSuites();
            System.out.println("enable len:" + enabledCipherSuites.length);
            System.out.println("enable :" + Arrays.toString(enabledCipherSuites));


            return new SSLConnectionSocketFactory(
                    sslContext, enabledProtocols, enabledCipherSuites,
                    NoopHostnameVerifier.INSTANCE);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}