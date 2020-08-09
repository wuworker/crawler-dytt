package com.wxl.dyttcrawler.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Create by wuxingle on 2020/8/8
 * term结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TermItem<K, V> implements Serializable {

    private static final long serialVersionUID = 719887731286001342L;

    private List<Item<K, V>> items;

    private Long otherSize;
}
