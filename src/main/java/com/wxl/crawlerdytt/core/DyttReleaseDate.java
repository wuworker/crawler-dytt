package com.wxl.crawlerdytt.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create by wuxingle on 2020/5/3
 * 上映时间
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DyttReleaseDate {

    // 时间yyyy-MM-dd
    private String date;

    // 地区
    private String place;
}
