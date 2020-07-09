package com.wxl.crawlerdytt.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Create by wuxingle on 2020/7/8
 * 爬虫消费进度
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerProgressBO implements Serializable {

    private static final long serialVersionUID = 3451429631429695332L;

    /**
     * 待处理数
     */
    private Integer todoSize;

    /**
     * 已处理数
     */
    private Integer totalSize;
}
