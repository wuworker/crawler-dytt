package com.wxl.dyttcrawler.web.dto.crawler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Create by wuxingle on 2020/7/19
 * 爬虫进度
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrawlerProgress implements Serializable {

    private static final long serialVersionUID = 4774966361642244570L;

    /**
     * 待处理数
     */
    private Integer todoSize = 0;

    /**
     * 处理总数
     */
    private Integer totalSize = 0;

    /**
     * 处理失败数
     */
    private Integer failSize = 0;
}
