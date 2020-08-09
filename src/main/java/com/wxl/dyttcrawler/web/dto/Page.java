package com.wxl.dyttcrawler.web.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Create by wuxingle on 2020/7/16
 * 分页结果
 */
@Data
public class Page<T> implements Serializable {

    private static final long serialVersionUID = 8740278756389681021L;

    private List<T> list;

    private long total;

    public Page() {
        this(Collections.emptyList(), 0L);
    }

    public Page(List<T> list, long total) {
        this.list = list;
        this.total = total;
    }
}
