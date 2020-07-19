package com.wxl.dyttcrawler.downloader;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.HttpContext;

import java.net.URI;

/**
 * Create by wuxingle on 2020/7/18
 * 电影天堂的重定向策略
 * 200也可能是重定向。
 */
@Slf4j
public class DyttRedirectStrategy extends LaxRedirectStrategy {


    @Override
    public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        boolean isMoved = super.isRedirected(request, response, context);
        if (!isMoved) {
            final String method = request.getRequestLine().getMethod();
            if (isRedirectable(method)) {
                final Header locationHeader = response.getFirstHeader(HttpHeaders.CONTENT_LOCATION);
                if (locationHeader != null) {
                    log.debug("{},has content-location header:{}",
                            request.getRequestLine().getUri(), locationHeader.getValue());
                    response.addHeader("location", locationHeader.getValue());
                    return true;

                }
            }
            return false;
        }
        return true;
    }

    @Override
    public URI getLocationURI(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        return super.getLocationURI(request, response, context);
    }
}
