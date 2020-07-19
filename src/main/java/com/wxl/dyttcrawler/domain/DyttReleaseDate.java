package com.wxl.dyttcrawler.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Create by wuxingle on 2020/5/3
 * 上映时间
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DyttReleaseDate implements Serializable {

    private static final long serialVersionUID = -5634712742357231640L;

    // 时间
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;

    // 地区
    private String place;
}
