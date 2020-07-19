package com.wxl.dyttcrawler.web.dto.crawler;

import lombok.Data;

import java.io.Serializable;

/**
 * Create by wuxingle on 2020/7/18
 * 指定页面重新爬取
 */
@Data
public class ManualUrl implements Serializable {

    private static final long serialVersionUID = 2306533594230568173L;

    private String url;
}
