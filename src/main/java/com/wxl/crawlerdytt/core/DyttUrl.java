package com.wxl.crawlerdytt.core;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Create by wuxingle on 2020/5/1
 * 电影天堂url
 */
@Data
@Builder
public class DyttUrl implements Serializable {

    private static final long serialVersionUID = 6647570962420163236L;

    private String url;

    private String path;

    private int layer;


}
