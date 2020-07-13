package com.wxl.crawlerdytt.core;

/**
 * Create by wuxingle on 2020/5/3
 * 常量
 */
public interface DyttConstants {

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
