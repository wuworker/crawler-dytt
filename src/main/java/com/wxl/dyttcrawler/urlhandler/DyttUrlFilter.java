package com.wxl.dyttcrawler.urlhandler;

import com.wxl.dyttcrawler.core.DyttConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URL;

/**
 * Create by wuxingle on 2020/5/16
 * url匹配
 * 协议，域名必须匹配
 */
@Slf4j
@Component
public class DyttUrlFilter implements UrlFilter {

    /**
     * 是否匹配
     */
    @Override
    public boolean filter(URL url) {
        String protocol = url.getProtocol();
        if (!DyttConstants.PROTOCOL_PATTERN.matcher(protocol).matches()) {
            return false;
        }
        String host = url.getHost();
        if (!DyttConstants.DOMAIN_PATTERN.matcher(host).matches()) {
            return false;
        }

        return true;
    }
}
