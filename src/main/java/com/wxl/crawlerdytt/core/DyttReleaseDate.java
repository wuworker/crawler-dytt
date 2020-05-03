package com.wxl.crawlerdytt.core;

import lombok.Data;

import java.util.Date;

/**
 * Create by wuxingle on 2020/5/3
 * 上映时间
 */
@Data
public class DyttReleaseDate {

    // 时间
    private Date date;

    // 地区
    private String place;
}
