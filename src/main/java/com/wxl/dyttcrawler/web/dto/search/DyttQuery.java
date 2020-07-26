package com.wxl.dyttcrawler.web.dto.search;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Create by wuxingle on 2020/7/16
 * 电影查询
 */
@Data
public class DyttQuery implements Serializable {

    private static final long serialVersionUID = 2575174730859857357L;

    // 标题,片名,译名
    private String title;

    // 年代起始
    private Integer yearStart;

    // 年代结束
    private Integer yearEnd;

    // 产地
    private List<String> originPlace;

    // 类别
    private List<String> category;

    // 主演,导演,编剧
    private String people;

    // 标签
    private List<String> tags;

    // 起始位置
    private Integer from;

    // 查询大小
    private Integer size;


    public void initFromSize() {
        if (from == null || from < 0) {
            from = 0;
        }
        if (size == null || size <= 0) {
            size = 10;
        }
    }

}
