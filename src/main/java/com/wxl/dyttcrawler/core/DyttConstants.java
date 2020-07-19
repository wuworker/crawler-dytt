package com.wxl.dyttcrawler.core;

import java.util.regex.Pattern;

/**
 * Create by wuxingle on 2020/5/3
 * 常量
 */
public interface DyttConstants {

    /**
     * 爬取协议正则
     */
    Pattern PROTOCOL_PATTERN = Pattern.compile("http|https");

    /**
     * 爬取域名正则
     */
    Pattern DOMAIN_PATTERN = Pattern.compile("(www\\.dytt8\\.net)|(www\\.ygdy8\\.com)");

    /**
     * 网站首页
     */
    Pattern INDEX_PATTERN = Pattern.compile("/?|/index\\.html?");

    /**
     * 国内电影首页
     */
    Pattern GNDY_INDEX_PATTERN = Pattern.compile("/html/gndy(/?|(/index\\.html)?)");

    /**
     * 国内电影详情页路径
     */
    Pattern GNDY_DETAIL_PATH_PATTERN = Pattern.compile("/html/gndy/\\w+/(\\d{4})(\\d{2})(\\d{2})/(\\d+)\\.html");

    /**
     * 电影分类列表页路径
     */
    Pattern CATEGORY_LIST_PATH_PATTERN = Pattern.compile("/html/gndy/\\w+(/?|(/index\\.html)?)");


    /**
     * 请求属性
     */
    interface RequestAttr {

        /**
         * 链接深度
         */
        String DEPTH = "_depth";
    }


    /**
     * es
     */
    interface Elastic {

        String DYTT_INDEX = "dytt";

        String DYTT_TYPE = "_doc";
    }

}
